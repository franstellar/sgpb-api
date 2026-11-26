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

    @PutMapping("/{eventId}")
    public Event updateEvent(@PathVariable Long eventId, @RequestBody Event updated) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (updated.getTitle() != null) event.setTitle(updated.getTitle());
        if (updated.getDescription() != null) event.setDescription(updated.getDescription());
        if (updated.getEventDate() != null) event.setEventDate(updated.getEventDate());
        if (updated.getPickupStart() != null) event.setPickupStart(updated.getPickupStart());
        if (updated.getPickupEnd() != null) event.setPickupEnd(updated.getPickupEnd());
        if (updated.getLocation() != null) event.setLocation(updated.getLocation());
        if (updated.getStatus() != null) event.setStatus(updated.getStatus());

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
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        return productRepository.save(product);
    }

    @PutMapping("/products/{productId}")
    public Product updateProduct(@PathVariable Long productId, @RequestBody Product updated) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        if (updated.getName() != null) product.setName(updated.getName());
        if (updated.getDescription() != null) product.setDescription(updated.getDescription());
        if (updated.getPrice() != null) product.setPrice(updated.getPrice());
        if (updated.getStockLimit() != null) product.setStockLimit(updated.getStockLimit());
        if (updated.getIsActive() != null) product.setIsActive(updated.getIsActive());
        if (updated.getImageUrl() != null) product.setImageUrl(updated.getImageUrl());

        return productRepository.save(product);
    }

    @GetMapping("/{eventId}/batches")
    public List<Batch> listBatchesByEvent(@PathVariable Long eventId) {
        return batchRepository.findByEventId(eventId);
    }

    // Rota Admin: Criar novo lote vinculado ao evento (Ex: 2º Lote)
    @PostMapping("/{eventId}/batches")
    public Batch createBatch(@PathVariable Long eventId, @RequestBody Batch batch) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        batch.setEvent(event);
        return batchRepository.save(batch);
    }

    // Rota Admin: Pausar/Ativar vendas de um produto
    @PatchMapping("/products/{productId}/toggle")
    public Product toggleProductStatus(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        boolean current = product.getIsActive() != null ? product.getIsActive() : true;
        product.setIsActive(!current);
        return productRepository.save(product);
    }
}