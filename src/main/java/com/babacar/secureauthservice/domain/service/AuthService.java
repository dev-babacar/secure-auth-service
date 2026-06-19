package com.babacar.secureauthservice.domain.service;


import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.out.AuditLogRepository;
import com.babacar.secureauthservice.domain.port.out.UserRepository;

public class AuthService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AuthService(UserRepository userRepository,
                       AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public User register(String email, String passwordHash) {
        // règle métier : un email doit être unique
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already registered: " + email
            );
        }

        User user = new User(
                java.util.UUID.randomUUID(),
                email,
                passwordHash,
                Role.USER,
                false
        );

        User saved = userRepository.save(user);
        auditLogRepository.log(saved.id(), "REGISTER_SUCCESS", null);
        return saved;
    }

    public User authenticate(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + email
                ));
    }
}
