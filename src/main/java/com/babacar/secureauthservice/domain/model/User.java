package com.babacar.secureauthservice.domain.model;

import java.util.UUID;

public record User(
        UUID id,
        String email,
        String passwordHash,
        Role role,
        boolean mfaEnabled
) {}
