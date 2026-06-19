package com.babacar.secureauthservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
        UUID id,
        String tokenHash,
        UUID userId,
        Instant expiresAt,
        boolean revoked,
        UUID family
) {}
