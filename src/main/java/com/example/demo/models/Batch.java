package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "batches")
public class Batch extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String name;

    @Column(name = "sale_start", nullable = false)
    private LocalDateTime saleStart;

    @Column(name = "sale_end", nullable = false)
    private LocalDateTime saleEnd;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}