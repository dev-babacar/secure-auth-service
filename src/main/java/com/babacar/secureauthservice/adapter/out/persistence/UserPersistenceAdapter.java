package com.babacar.secureauthservice.adapter.out.persistence;

import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.out.UserRepository;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User updateMfaSecret(UUID userId, String secret) {
        UserEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable"
                ));
        entity.setMfaSecret(secret);
        entity.setMfaVerified(false);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public User enableMfa(UUID userId) {
        UserEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable"
                ));
        entity.setMfaVerified(true);
        return toDomain(jpaRepository.save(entity));
    }

    // ─── Mappers ───

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.email(),
                user.passwordHash(),
                RoleEntity.valueOf(user.role().name()),
                user.mfaEnabled(),
                Instant.now(),
                user.mfaSecret(),
                user.mfaVerified()
        );
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                Role.valueOf(entity.getRole().name()),
                entity.isMfaEnabled(),
                entity.getMfaSecret(),
                entity.isMfaVerified()
        );
    }
}
