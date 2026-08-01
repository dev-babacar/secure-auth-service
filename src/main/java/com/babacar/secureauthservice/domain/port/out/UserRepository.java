package com.babacar.secureauthservice.domain.port.out;
import com.babacar.secureauthservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
    User updateMfaSecret(UUID userId, String secret);  // ← ajouté
    User enableMfa(UUID userId);
}
