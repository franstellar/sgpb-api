package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String origin; // WEB, APP, PARTNER, BALCAO

    @Column(nullable = false)
    private String status; // PENDING, PAID, PICKED_UP, EXPIRED, CANCELLED, REFUNDED

    @Column(name = "donation_amount", precision = 10, scale = 2)
    private BigDecimal donationAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(length = 500)
    private String pixCopyPaste;
}