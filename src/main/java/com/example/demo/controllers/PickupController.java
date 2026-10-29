package com.example.demo.controllers;

import com.example.demo.models.Order;
import com.example.demo.models.PickupReceipt;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.PickupValidationRepository;
import com.example.demo.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pickups")
public class PickupController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PickupValidationRepository pickupValidationRepository;

    public PickupController(
            OrderService orderService,
            OrderRepository orderRepository,
            PickupValidationRepository pickupValidationRepository
    ) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.pickupValidationRepository = pickupValidationRepository;
    }

    public record ValidatePickupRequest(String orderNumber, Long volunteerUserId, String validationMethod) {}

    // Rota para o aplicativo mobile baixar a lista de pedidos pagos para o SQLite local (Modo de Contingência)
    @GetMapping("/sync-data")
    public List<Map<String, Object>> getOrdersForOfflineSync() {
        List<Order> paidOrders = orderRepository.findAll().stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .toList();

        return paidOrders.stream().map(order -> Map.<String, Object>of(
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "buyerName", order.getUser().getName(),
                "totalAmount", order.getTotalAmount(),
                "alreadyPickedUp", pickupValidationRepository.findByOrderId(order.getId()).isPresent()
        )).toList();
    }

    // Validação online direta por QR Code ou código manual
    @PostMapping("/validate")
    public ResponseEntity<?> validatePickup(@RequestBody ValidatePickupRequest request) {
        try {
            PickupReceipt receipt = orderService.validatePickup(
                    request.orderNumber(),
                    request.volunteerUserId(),
                    request.validationMethod()
            );
            return ResponseEntity.ok(receipt);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Recebe o lote de validações feitas offline no SQLite do celular e sincroniza com o servidor
    @PostMapping("/sync-offline")
    public ResponseEntity<?> syncOfflinePickups(@RequestBody List<ValidatePickupRequest> offlineValidations) {
        List<String> synced = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (ValidatePickupRequest req : offlineValidations) {
            try {
                orderService.validatePickup(req.orderNumber(), req.volunteerUserId(), req.validationMethod());
                synced.add(req.orderNumber());
            } catch (RuntimeException e) {
                errors.add(req.orderNumber() + ": " + e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
                "syncedCount", synced.size(),
                "syncedOrders", synced,
                "errors", errors
        ));
    }
}