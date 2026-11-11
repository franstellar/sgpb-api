package com.example.demo.controllers;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Controle em memória para NF3.1: limite de 5 tentativas de login e bloqueio de 5 minutos
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockoutUntil = new ConcurrentHashMap<>();

    public AuthController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public record LoginRequest(String email, String password) {}

    public record RegisterRequest(String name, String email, String phone, String password) {}

    public record CreateStaffRequest(
            Long adminUserId,
            String name,
            String email,
            String phone,
            String password,
            String roleName // VOLUNTEER, PARTNER ou ADMIN
    ) {}

    // 1. Login com trava de 5 tentativas (NF3.1)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String emailKey = request.email() != null ? request.email().trim().toLowerCase() : "";

        // Verifica se a conta está temporariamente bloqueada (5 minutos)
        if (lockoutUntil.containsKey(emailKey)) {
            LocalDateTime lockedTime = lockoutUntil.get(emailKey);
            if (LocalDateTime.now().isBefore(lockedTime)) {
                System.out.println("🔒 Tentativa de acesso em conta bloqueada: " + emailKey);
                return ResponseEntity.status(429).body(Map.of(
                        "error", "Conta bloqueada temporariamente por 5 minutos após 5 tentativas inválidas."
                ));
            } else {
                lockoutUntil.remove(emailKey);
                failedAttempts.remove(emailKey);
            }
        }

        var userOpt = userRepository.findByEmail(request.email())
                .filter(user -> user.getPasswordHash().equals(request.password()));

        if (userOpt.isPresent()) {
            failedAttempts.remove(emailKey);
            lockoutUntil.remove(emailKey);
            User user = userOpt.get();

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "phone", user.getPhone() != null ? user.getPhone() : "",
                    "role", user.getRole().getName()
            ));
        }

        // Registra falha e bloqueia se atingir 5 tentativas (NF3.1)
        int attempts = failedAttempts.getOrDefault(emailKey, 0) + 1;
        failedAttempts.put(emailKey, attempts);

        if (attempts >= 5) {
            lockoutUntil.put(emailKey, LocalDateTime.now().plusMinutes(5));
            System.out.println("🚨 ALERTA DE SEGURANÇA (NF3.1): Usuário " + emailKey + " bloqueado por 5 minutos após 5 falhas de login.");
            return ResponseEntity.status(429).body(Map.of(
                    "error", "Limite de 5 tentativas excedido. Acesso bloqueado por 5 minutos."
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
                "error", "Credenciais inválidas. Tentativa " + attempts + " de 5."
        ));
    }

    // 2. Autocadastro exclusivo para Comprador (F3 - Parte 1)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "E-mail já cadastrado"));
        }

        Role buyerRole = roleRepository.findByName("BUYER")
                .orElseThrow(() -> new RuntimeException("Perfil BUYER não encontrado"));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPasswordHash(request.password() != null && !request.password().isBlank() ? request.password() : "123456");
        user.setRole(buyerRole);
        user = userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "role", user.getRole().getName()
        ));
    }

    // 3. Cadastro de Equipe (VOLUNTEER, PARTNER, ADMIN) exclusivo pelo Administrador (F3 - Parte 2)
    @PostMapping("/staff")
    public ResponseEntity<?> registerStaff(@RequestBody CreateStaffRequest request) {
        User admin = userRepository.findById(request.adminUserId())
                .orElseThrow(() -> new RuntimeException("Usuário administrador não encontrado"));

        if (!"ADMIN".equals(admin.getRole().getName())) {
            return ResponseEntity.status(403).body(Map.of(
                    "error", "Acesso negado: apenas o Organizador (ADMIN) pode cadastrar membros da equipe."
            ));
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "E-mail já cadastrado"));
        }

        String targetRoleName = request.roleName() != null ? request.roleName().toUpperCase() : "VOLUNTEER";
        if (!List.of("VOLUNTEER", "PARTNER", "ADMIN").contains(targetRoleName)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Perfil inválido para equipe. Use VOLUNTEER, PARTNER ou ADMIN."
            ));
        }

        Role staffRole = roleRepository.findByName(targetRoleName)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + targetRoleName));

        User staffUser = new User();
        staffUser.setName(request.name());
        staffUser.setEmail(request.email());
        staffUser.setPhone(request.phone());
        staffUser.setPasswordHash(request.password());
        staffUser.setRole(staffRole);
        staffUser = userRepository.save(staffUser);

        return ResponseEntity.ok(Map.of(
                "id", staffUser.getId(),
                "name", staffUser.getName(),
                "email", staffUser.getEmail(),
                "phone", staffUser.getPhone() != null ? staffUser.getPhone() : "",
                "role", staffUser.getRole().getName()
        ));
    }

    // 4. Listar usuários da equipe para o painel do Admin
    @GetMapping("/staff")
    public List> listStaff() {
        return userRepository.findAll().stream()
                .filter(u -> !"BUYER".equals(u.getRole().getName()))
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "name", u.getName(),
                        "email", u.getEmail(),
                        "phone", u.getPhone() != null ? u.getPhone() : "",
                        "role", u.getRole().getName()
                ))
                .toList();
    }
}