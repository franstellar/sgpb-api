package com.example.demo.repositories;

import com.example.demo.models.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByEventId(Long eventId);
}