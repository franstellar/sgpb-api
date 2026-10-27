package com.example.demo.repositories;

import com.example.demo.models.BatchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BatchProductRepository extends JpaRepository<BatchProduct, Long> {
    List<BatchProduct> findByBatchId(Long batchId);
}