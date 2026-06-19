package com.babacar.secureauthservice.domain.model;

public record MfaSecret(String value) {
    public MfaSecret {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(
                    "MFA secret cannot be blank"
            );
    }
}
