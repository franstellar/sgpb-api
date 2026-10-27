package com.example.demo.repositories;

import com.example.demo.models.PickupReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PickupReceiptRepository extends JpaRepository<PickupReceipt, Long> {
    Optional<PickupReceipt> findByReceiptCode(String receiptCode);
}