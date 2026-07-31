package com.babacar.secureauthservice.domain.service;


import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.port.out.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken issue(UUID userId, String tokenHash) {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                tokenHash,
                userId,
                Instant.now().plusSeconds(7 * 24 * 3600), // 7 jours
                false,
                UUID.randomUUID() // nouvelle famille
        );
        return refreshTokenRepository.save(token);
    }

    public RefreshToken rotate(String oldTokenHash, String newTokenHash) {
        RefreshToken old = refreshTokenRepository
                .findByTokenHash(oldTokenHash)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Token introuvable ou expiré"
                ));

        // Détection de vol — ADR-005
        if (old.revoked()) {
            refreshTokenRepository.revokeAllByFamily(old.family());
            throw new IllegalStateException(
                    "Token réutilisé détecté — session révoquée"
            );
        }

        // Révoque l'ancien token
        RefreshToken revoked = new RefreshToken(
                old.id(),
                old.tokenHash(),
                old.userId(),
                old.expiresAt(),
                true,           // révoqué
                old.family()
        );
        refreshTokenRepository.save(revoked);

        // Émet le nouveau token — même famille
        RefreshToken newToken = new RefreshToken(
                UUID.randomUUID(),
                newTokenHash,
                old.userId(),
                Instant.now().plusSeconds(7 * 24 * 3600),
                false,
                old.family()    // même famille
        );
        return refreshTokenRepository.save(newToken);
    }
}
