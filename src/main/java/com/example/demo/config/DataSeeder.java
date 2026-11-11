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
            if (roleRepository.count() == 0) {
                System.out.println("🌱 Iniciando Seeder do banco de dados SGPB...");

                // 1. Criar os 4 Perfis Oficiais (Roles)
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
                partnerRole = roleRepository.save(partnerRole);

                // 2. Criar Usuários iniciais para os 4 perfis
                User admin = new User();
                admin.setName("Administrador ABADEUS");
                admin.setEmail("admin@abadeus.com.br");
                admin.setPhone("48999990000");
                admin.setPasswordHash("ADM123");
                admin.setRole(adminRole);
                userRepository.save(admin);

                User volunteer = new User();
                volunteer.setName("Voluntário Fila Drive-Thru");
                volunteer.setEmail("voluntario@abadeus.com.br");
                volunteer.setPhone("48999991111");
                volunteer.setPasswordHash("VOL123");
                volunteer.setRole(volunteerRole);
                userRepository.save(volunteer);

                User partner = new User();
                partner.setName("Parceiro Ponto de Venda");
                partner.setEmail("parceiro@abadeus.com.br");
                partner.setPhone("48999992222");
                partner.setPasswordHash("PAR123");
                partner.setRole(partnerRole);
                userRepository.save(partner);

                User buyer = new User();
                buyer.setName("Maria Clara Silveira");
                buyer.setEmail("mariaclara.silveira@email.com");
                buyer.setPhone("48998421082");
                buyer.setPasswordHash("123456");
                buyer.setRole(buyerRole);
                userRepository.save(buyer);

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

                // 4. Criar os 4 Produtos alinhados ao Figma
                Product p1 = new Product();
                p1.setEvent(event);
                p1.setName("Paella Tradicional Individual");
                p1.setDescription("Arroz bomba artesanal, açafrão espanhol, camarões médios, anéis de lula, mexilhões e lascas de frango (450g).");
                p1.setPrice(new BigDecimal("45.00"));
                p1.setStockLimit(300);
                p1.setIsActive(true);
                p1.setImageUrl("https://images.unsplash.com/photo-1534080564583-6be75777b70a?auto=format&fit=crop&w=800&q=80");
                p1 = productRepository.save(p1);

                Product p2 = new Product();
                p2.setEvent(event);
                p2.setName("Paella Família Solidária");
                p2.setDescription("Serve até 4 pessoas (1,2kg). Dobro de frutos do mar nobres, camarões pistola grelhados e tempero artesanal.");
                p2.setPrice(new BigDecimal("120.00"));
                p2.setStockLimit(150);
                p2.setIsActive(true);
                p2.setImageUrl("https://images.unsplash.com/photo-1515443961218-a51367888e4b?auto=format&fit=crop&w=800&q=80");
                p2 = productRepository.save(p2);

                Product p3 = new Product();
                p3.setEvent(event);
                p3.setName("Combo Paelito + Churros");
                p3.setDescription("1 Porção Individual generosa (450g) + 4 Minichurros artesanais crocantes com doce de leite caseiro da ABADEUS.");
                p3.setPrice(new BigDecimal("65.00"));
                p3.setStockLimit(100);
                p3.setIsActive(true);
                p3.setImageUrl("https://images.unsplash.com/photo-1626804475297-41608ea09aeb?auto=format&fit=crop&w=800&q=80");
                p3 = productRepository.save(p3);

                Product p4 = new Product();
                p4.setEvent(event);
                p4.setName("Paella Veggie");
                p4.setDescription("Mix de cogumelos frescos, alcachofras, pimentões assados e açafrão espanhol (450g).");
                p4.setPrice(new BigDecimal("40.00"));
                p4.setStockLimit(80);
                p4.setIsActive(true);
                p4.setImageUrl("https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=800&q=80");
                p4 = productRepository.save(p4);

                // 5. Criar 1º Lote e vincular produto
                Batch batch = new Batch();
                batch.setEvent(event);
                batch.setName("1º Lote Solidário");
                batch.setSaleStart(LocalDateTime.now().minusDays(1));
                batch.setSaleEnd(LocalDateTime.of(2026, 11, 27, 23, 59));
                batch.setIsActive(true);
                batch = batchRepository.save(batch);

                BatchProduct bp1 = new BatchProduct();
                bp1.setBatch(batch);
                bp1.setProduct(p1);
                bp1.setStockLimit(300);
                bp1.setBatchPrice(new BigDecimal("45.00"));
                batchProductRepository.save(bp1);

                System.out.println("✅ Seeder concluído! Logins criados: admin@abadeus.com.br (ADM123), voluntario@abadeus.com.br (VOL123), parceiro@abadeus.com.br (PAR123)");
            }
        };
    }
}