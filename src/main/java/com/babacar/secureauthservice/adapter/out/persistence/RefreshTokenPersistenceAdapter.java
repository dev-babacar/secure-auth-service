package com.babacar.secureauthservice.adapter.out.persistence;

import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.port.out.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        // Cherche si le token existe déjà en base
        return jpaRepository.findById(token.id())
                .map(existing -> {
                    // UPDATE — met à jour le champ revoked
                    existing.setRevoked(token.revoked());
                    return toDomain(jpaRepository.save(existing));
                })
                .orElseGet(() -> {
                    // INSERT — nouveau token
                    RefreshTokenEntity entity = toEntity(token);
                    return toDomain(jpaRepository.save(entity));
                });
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String hash) {
        return jpaRepository.findByTokenHash(hash)
                .map(this::toDomain);
    }

    @Override
    public void revokeAllByFamily(UUID family) {
        jpaRepository.revokeAllByFamily(family);
    }

    // ─── Mappers ───

    private RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(
                token.id(),
                token.tokenHash(),
                token.userId(),
                token.expiresAt(),
                token.revoked(),
                token.family()
        );
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getTokenHash(),
                entity.getUserId(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getFamily()
        );
    }
}
