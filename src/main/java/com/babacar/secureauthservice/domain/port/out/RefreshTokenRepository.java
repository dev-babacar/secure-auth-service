package com.babacar.secureauthservice.domain.port.out;

import com.babacar.secureauthservice.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String hash);
    void revokeAllByFamily(UUID family);
}
