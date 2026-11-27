package com.example.demo.config;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            ProductRepository productRepository,
            BatchRepository batchRepository,
            BatchProductRepository batchProductRepository
    ) {
        return args -> {
            if (roleRepository.count() == 0) {
                System.out.println("🌱 Iniciando Seeder do banco de dados SGPB...");

                // 1. Criar os 4 Perfis Oficiais (Roles)
                Role adminRole = saveRole(roleRepository, "ADMIN", "Administrador/Organizador");
                Role buyerRole = saveRole(roleRepository, "BUYER", "Comprador/Apoiador da campanha");
                Role volunteerRole = saveRole(roleRepository, "VOLUNTEER", "Voluntário/Equipe de entrega");
                Role partnerRole = saveRole(roleRepository, "PARTNER", "Vendedor/Parceiro de divulgação");

                // 2. Criar Usuários iniciais para os 4 perfis
                saveUser(userRepository, "Administrador ABADEUS", "admin@abadeus.com.br", "48999990000", "ADM123", adminRole);
                saveUser(userRepository, "Voluntário Fila Drive-Thru", "voluntario@abadeus.com.br", "48999991111", "VOL123", volunteerRole);
                saveUser(userRepository, "Parceiro Ponto de Venda", "parceiro@abadeus.com.br", "48999992222", "PAR123", partnerRole);
                saveUser(userRepository, "Cliente", "cliente@email.com", "48999999999", "123456", buyerRole);

                // 3. Criar Evento padrão
                Event event = new Event();
                event.setTitle("12ª Edição Solidária Paelito - ABADEUS");
                event.setDescription("Paella feita para compartilhar. Mais que uma paella, uma boa causa para aquecer corações!");
                event.setEventDate(LocalDate.of(2026, 11, 28));
                event.setPickupStart(LocalDateTime.of(2026, 11, 28, 11, 30));
                event.setPickupEnd(LocalDateTime.of(2026, 11, 28, 14, 30));
                event.setLocation("ABADEUS - Rua Maria Salete de Oliveira, 422 - Cristo Redentor, Criciúma/SC");
                event.setStatus("PUBLISHED");
                event = eventRepository.save(event);

                // 4. Criar os 17 Produtos alinhados ao App
                List<Product> products = Arrays.asList(
                        // Paellas de frutos do mar (IDs 1..3)
                        saveProduct(productRepository, event, "Paella de frutos do mar (individual)", "Camarão, lula e mexilhão (450g)", "50.00", 200, null),
                        saveProduct(productRepository, event, "Paella de frutos do mar (2 pessoas)", "Camarão, lula e mexilhão (900g)", "95.00", 120, null),
                        saveProduct(productRepository, event, "Paella de frutos do mar (4 pessoas)", "Camarão, lula e mexilhão (1,8kg)", "180.00", 80, null),

                        // Paellas de carnes (IDs 4..6)
                        saveProduct(productRepository, event, "Paella de carnes (individual)", "Frango, lombo suíno e linguiça (450g)", "45.00", 200, null),
                        saveProduct(productRepository, event, "Paella de carnes (2 pessoas)", "Frango, lombo suíno e linguiça (900g)", "85.00", 120, null),
                        saveProduct(productRepository, event, "Paella de carnes (4 pessoas)", "Frango, lombo suíno e linguiça (1,8kg)", "160.00", 80, null),

                        // Paellas veggie (IDs 7..9)
                        saveProduct(productRepository, event, "Paella veggie (individual)", "Cogumelos frescos, alcachofras e pimentões (450g)", "40.00", 100, null),
                        saveProduct(productRepository, event, "Paella veggie (2 pessoas)", "Cogumelos frescos, alcachofras e pimentões (900g)", "75.00", 60, null),
                        saveProduct(productRepository, event, "Paella veggie (4 pessoas)", "Cogumelos frescos, alcachofras e pimentões (1,8kg)", "140.00", 40, null),

                        // Combos Paelito (IDs 10..12)
                        saveProduct(productRepository, event, "Combo Paelito frutos do mar", "Porção individual + 4 minichurros artesanais + refrigerante lata ou água", "65.00", 100, "https://images.unsplash.com/photo-1624371414361-e670edf4898d?w=800"),
                        saveProduct(productRepository, event, "Combo Paelito carnes", "Porção individual + 4 minichurros artesanais + refrigerante lata ou água", "60.00", 100, "https://images.unsplash.com/photo-1624371414361-e670edf4898d?w=800"),
                        saveProduct(productRepository, event, "Combo Paelito veggie", "Porção individual + 4 minichurros artesanais + refrigerante lata ou água", "55.00", 80, "https://images.unsplash.com/photo-1624371414361-e670edf4898d?w=800"),

                        // Bebidas (IDs 13..17)
                        saveProduct(productRepository, event, "Refrigerante lata (350mL)", "Coca-Cola, Pepsi ou Guaraná", "6.00", 200, "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=800"),
                        saveProduct(productRepository, event, "Refrigerante 600mL", "Coca-Cola, Pepsi ou Guaraná", "9.00", 150, null),
                        saveProduct(productRepository, event, "Refrigerante 2L", "Coca-Cola, Pepsi ou Guaraná", "14.00", 120, null),
                        saveProduct(productRepository, event, "Água mineral (500mL)", "Com ou sem gás", "4.00", 200, null),
                        saveProduct(productRepository, event, "Suco integral (300mL)", "Uva ou laranja", "8.00", 150, "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=800")
                );

                // 5. Criar 1º Lote e vincular todos os produtos
                Batch batch = new Batch();
                batch.setEvent(event);
                batch.setName("1º Lote Solidário");
                batch.setSaleStart(LocalDateTime.now().minusDays(1));
                batch.setSaleEnd(LocalDateTime.of(2026, 11, 27, 23, 59));
                batch.setIsActive(true);
                batch = batchRepository.save(batch);

                for (Product product : products) {
                    BatchProduct bp = new BatchProduct();
                    bp.setBatch(batch);
                    bp.setProduct(product);
                    bp.setStockLimit(product.getStockLimit());
                    bp.setBatchPrice(product.getPrice());
                    batchProductRepository.save(bp);
                }

                System.out.println("✅ Seeder concluído! 17 produtos e 4 perfis criados.");
            }
        };
    }

    private Role saveRole(RoleRepository repo, String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return repo.save(role);
    }

    private User saveUser(UserRepository repo, String name, String email, String phone, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(password);
        user.setRole(role);
        return repo.save(user);
    }

    private Product saveProduct(
            ProductRepository repo,
            Event event,
            String name,
            String description,
            String price,
            int stockLimit,
            String imageUrl
    ) {
        Product p = new Product();
        p.setEvent(event);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(new BigDecimal(price));
        p.setStockLimit(stockLimit);
        p.setIsActive(true);
        p.setImageUrl(imageUrl);
        return repo.save(p);
    }
}