package com.example.demo.controllers;

import com.example.demo.models.Batch;
import com.example.demo.models.Event;
import com.example.demo.models.Product;
import com.example.demo.repositories.BatchRepository;
import com.example.demo.repositories.EventRepository;
import com.example.demo.repositories.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;

    public EventController(
            EventRepository eventRepository,
            ProductRepository productRepository,
            BatchRepository batchRepository
    ) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
    }

    @GetMapping
    public List<Event> listEvents() {
        return eventRepository.findAll();
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping("/{eventId}/products")
    public List<Product> listProductsByEvent(@PathVariable Long eventId) {
        return productRepository.findByEventId(eventId);
    }

    @PostMapping("/{eventId}/products")
    public Product createProduct(@PathVariable Long eventId, @RequestBody Product product) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        product.setEvent(event);
        return productRepository.save(product);
    }

    @GetMapping("/{eventId}/batches")
    public List<Batch> listBatchesByEvent(@PathVariable Long eventId) {
        return batchRepository.findByEventId(eventId);
    }
}