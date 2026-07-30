package com.babacar.secureauthservice.domain.model;

public record LoginResult(
        String accessToken,
        String refreshToken
) {}