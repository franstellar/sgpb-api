package com.example.demo.repositories;

import com.example.demo.models.PickupValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PickupValidationRepository extends JpaRepository<PickupValidation, Long> {
    Optional<PickupValidation> findByOrderId(Long orderId);
}