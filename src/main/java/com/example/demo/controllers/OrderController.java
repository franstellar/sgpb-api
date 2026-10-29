package com.example.demo.controllers;

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.models.Payment;
import com.example.demo.repositories.OrderItemRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.PickupValidationRepository;
import com.example.demo.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PickupValidationRepository pickupValidationRepository;

    public OrderController(
            OrderService orderService,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PickupValidationRepository pickupValidationRepository
    ) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pickupValidationRepository = pickupValidationRepository;
    }

    public record CreateOrderRequest(
            Long eventId,
            Long userId,
            String origin,
            List<OrderService.OrderItemInput> items
    ) {}

    public record PaymentWebhookRequest(String externalId, String method, String provider) {}

    @GetMapping
    public List<Order> listAllOrders(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return orderRepository.findByUserId(userId);
        }
        return orderRepository.findAll();
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> listOrderItems(@PathVariable Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrder(
                    request.eventId(),
                    request.userId(),
                    request.origin(),
                    request.items()
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId, @RequestBody PaymentWebhookRequest request) {
        try {
            Payment payment = orderService.confirmPayment(
                    orderId,
                    request.externalId(),
                    request.method(),
                    request.provider()
            );
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Painel de Indicadores (Seção 4 e 7 do PDF)
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardMetrics() {
        List<Order> allOrders = orderRepository.findAll();

        long paidOrdersCount = allOrders.stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .count();

        long pendingOrdersCount = allOrders.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .count();

        BigDecimal confirmedRevenue = allOrders.stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalUnitsSold = allOrders.stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .flatMap(o -> orderItemRepository.findByOrderId(o.getId()).stream())
                .mapToInt(OrderItem::getQuantity)
                .sum();

        long pickupsCompleted = pickupValidationRepository.count();
        long remainingPickups = Math.max(0, paidOrdersCount - pickupsCompleted);

        return Map.of(
                "totalUnitsSold", totalUnitsSold,
                "confirmedRevenue", confirmedRevenue,
                "pendingOrders", pendingOrdersCount,
                "completedPickups", pickupsCompleted,
                "remainingPickups", remainingPickups
        );
    }
}