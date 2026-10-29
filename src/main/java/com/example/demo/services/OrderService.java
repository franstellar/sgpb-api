package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final PickupValidationRepository pickupValidationRepository;
    private final PickupReceiptRepository pickupReceiptRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository,
            OrderStatusHistoryRepository historyRepository,
            PickupValidationRepository pickupValidationRepository,
            PickupReceiptRepository pickupReceiptRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.historyRepository = historyRepository;
        this.pickupValidationRepository = pickupValidationRepository;
        this.pickupReceiptRepository = pickupReceiptRepository;
    }

    public record OrderItemInput(Long productId, Integer quantity) {}

    // 1. Criar pedido e reservar estoque por 15 minutos (NF2.1)
    @Transactional
    public Order createOrder(Long eventId, Long userId, String origin, List<OrderItemInput> items) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setEvent(event);
        order.setUser(user);
        order.setOrderNumber("PAEL-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setOrigin(origin != null ? origin : "APP");
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemInput itemInput : items) {
            Product product = productRepository.findById(itemInput.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockLimit() < itemInput.quantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Deduz do estoque disponível enquanto está reservado
            product.setStockLimit(product.getStockLimit() - itemInput.quantity());
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemInput.quantity()));
            total = total.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemInput.quantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);
            orderItemRepository.save(orderItem);
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);

        recordHistory(order, null, "PENDING", user, "Order created with 15m stock reservation");
        return order;
    }

    // 2. Confirmar pagamento de forma idempotente (NF1.2)
    @Transactional
    public Payment confirmPayment(Long orderId, String externalId, String method, String provider) {
        // Se o webhook já enviou esse externalId antes, retorna o pagamento existente sem duplicar
        var existingPayment = paymentRepository.findByExternalId(externalId);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("EXPIRED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot pay for an expired or cancelled order");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setExternalId(externalId);
        payment.setMethod(method);
        payment.setProvider(provider != null ? provider : "PIX_PROVIDER");
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("CONFIRMED");
        payment.setConfirmedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        String oldStatus = order.getStatus();
        order.setStatus("PAID");
        orderRepository.save(order);

        recordHistory(order, oldStatus, "PAID", order.getUser(), "Confirmed via provider: " + externalId);
        return payment;
    }

    // 3. Validar retirada única por QR Code ou código manual e gerar comprovante
    @Transactional
    public PickupReceipt validatePickup(String orderNumber, Long volunteerUserId, String validationMethod) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));

        if (!"PAID".equals(order.getStatus())) {
            throw new RuntimeException("Order is not PAID. Current status: " + order.getStatus());
        }

        if (pickupValidationRepository.findByOrderId(order.getId()).isPresent()) {
            throw new RuntimeException("Order has already been picked up!");
        }

        User volunteer = userRepository.findById(volunteerUserId)
                .orElseThrow(() -> new RuntimeException("Volunteer user not found"));

        PickupValidation validation = new PickupValidation();
        validation.setOrder(order);
        validation.setValidatedBy(volunteer);
        validation.setValidatedAt(LocalDateTime.now());
        validation.setValidationMethod(validationMethod != null ? validationMethod : "QR_CODE");
        validation = pickupValidationRepository.save(validation);

        PickupReceipt receipt = new PickupReceipt();
        receipt.setPickupValidation(validation);
        receipt.setReceiptCode("REC-" + order.getOrderNumber());
        receipt.setIssuedAt(LocalDateTime.now());
        receipt = pickupReceiptRepository.save(receipt);

        recordHistory(order, "PAID", "PICKED_UP", volunteer, "Picked up via " + validation.getValidationMethod());
        return receipt;
    }

    // 4. Rotina automática: roda a cada 60s e devolve ao estoque pedidos que passaram de 15 min
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingOrders() {
        List<Order> expiredOrders = orderRepository.findByStatusAndExpiresAtBefore("PENDING", LocalDateTime.now());
        for (Order order : expiredOrders) {
            order.setStatus("EXPIRED");
            orderRepository.save(order);

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                Product product = item.getProduct();
                product.setStockLimit(product.getStockLimit() + item.getQuantity());
                productRepository.save(product);
            }

            recordHistory(order, "PENDING", "EXPIRED", null, "15-minute reservation expired automatically");
            System.out.println("⏰ Pedido expirado e estoque devolvido: " + order.getOrderNumber());
        }
    }

    private void recordHistory(Order order, String oldStatus, String newStatus, User changedBy, String justification) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        history.setJustification(justification);
        historyRepository.save(history);
    }
}