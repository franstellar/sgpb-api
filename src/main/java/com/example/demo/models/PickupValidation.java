package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pickup_validations")
public class PickupValidation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "validated_by", nullable = false)
    private User validatedBy;

    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt = LocalDateTime.now();

    @Column(name = "validation_method", nullable = false)
    private String validationMethod; // QR_CODE, MANUAL_CODE
}