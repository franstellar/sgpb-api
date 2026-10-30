package com.example.demo.config;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
            // Só executa se o banco ainda não tiver perfis cadastrados
            if (roleRepository.count() == 0) {
                System.out.println("🌱 Iniciando Seeder do banco de dados SGPB...");

                // 1. Criar Perfis (Roles)
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole.setDescription("Administrador/Organizador");
                adminRole = roleRepository.save(adminRole);

                Role buyerRole = new Role();
                buyerRole.setName("BUYER");
                buyerRole.setDescription("Comprador/Apoiador da campanha");
                buyerRole = roleRepository.save(buyerRole);

                Role volunteerRole = new Role();
                volunteerRole.setName("VOLUNTEER");
                volunteerRole.setDescription("Voluntário/Equipe de entrega");
                volunteerRole = roleRepository.save(volunteerRole);

                Role partnerRole = new Role();
                partnerRole.setName("PARTNER");
                partnerRole.setDescription("Vendedor/Parceiro de divulgação");
                roleRepository.save(partnerRole);

                // 2. Criar Usuários iniciais (incluindo o ADM123 do quadro)
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@abadeus.com.br");
                admin.setPhone("48999990000");
                admin.setPasswordHash("ADM123");
                admin.setRole(adminRole);
                userRepository.save(admin);

                User volunteer = new User();
                volunteer.setName("Voluntário entrega");
                volunteer.setEmail("voluntario@abadeus.com.br");
                volunteer.setPhone("48999991111");
                volunteer.setPasswordHash("VOL123");
                volunteer.setRole(volunteerRole);
                userRepository.save(volunteer);

                // 3. Criar Evento padrão
                Event event = new Event();
                event.setTitle("Paella Beneficente da ABADEUS - Edição 2026");
                event.setDescription("Mais que uma paella, uma boa causa! Escolha sua porção e apoie os projetos sociais da ABADEUS");
                event.setEventDate(LocalDate.of(2026, 11, 15));
                event.setPickupStart(LocalDateTime.of(2026, 11, 15, 11, 30));
                event.setPickupEnd(LocalDateTime.of(2026, 11, 15, 14, 0));
                event.setLocation("Sede da ABADEUS - Criciúma/SC");
                event.setStatus("PUBLISHED");
                event = eventRepository.save(event);

                // 4. Criar os 3 Produtos da identidade visual Paelito
                Product p1 = new Product();
                p1.setEvent(event);
                p1.setName("Paella individual");
                p1.setDescription("Porção individual de paella (aprox. 450g) ideal para 1 pessoa");
                p1.setPrice(new BigDecimal("45.00"));
                p1.setStockLimit(300);
                p1.setIsActive(true);
                p1.setImageUrl("https://images.unsplash.com/photo-1534080564583-6be75777b70a?auto=format&fit=crop&w=800&q=80");
                p1 = productRepository.save(p1);

                Product p2 = new Product();
                p2.setEvent(event);
                p2.setName("Paella familiar");
                p2.setDescription("Porção generosa feita para compartilhar (serve até 3 pessoas)");
                p2.setPrice(new BigDecimal("120.00"));
                p2.setStockLimit(150);
                p2.setIsActive(true);
                p2.setImageUrl("https://images.unsplash.com/photo-1515443961218-a51367888e4b?auto=format&fit=crop&w=800&q=80");
                p2 = productRepository.save(p2);

                Product p3 = new Product();
                p3.setEvent(event);
                p3.setName("Combo especial");
                p3.setDescription("2 Paellas familiares + 1 Paella individual com desconto solidário");
                p3.setPrice(new BigDecimal("260.00"));
                p3.setStockLimit(50);
                p3.setIsActive(true);
                p3.setImageUrl("https://images.unsplash.com/photo-1623961990059-28356e226a77?auto=format&fit=crop&w=800&q=80");
                p3 = productRepository.save(p3);

                // 5. Criar 1º Lote e vincular produtos
                Batch batch = new Batch();
                batch.setEvent(event);
                batch.setName("1º Lote solidário");
                batch.setSaleStart(LocalDateTime.now().minusDays(1));
                batch.setSaleEnd(LocalDateTime.of(2026, 11, 14, 23, 59));
                batch.setIsActive(true);
                batch = batchRepository.save(batch);

                BatchProduct bp1 = new BatchProduct();
                bp1.setBatch(batch);
                bp1.setProduct(p1);
                bp1.setStockLimit(300);
                bp1.setBatchPrice(new BigDecimal("45.00"));
                batchProductRepository.save(bp1);

                System.out.println("✅ Seeder concluído com sucesso! Usuário admin@abadeus.com.br (Senha: ADM123) criado");
            }
        };
    }
}