package com.example.demo.repositories;

import com.example.demo.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserId(Long userId);
    List<Order> findByEventId(Long eventId);
    List<Order> findByStatusAndExpiresAtBefore(String status, LocalDateTime now);
}