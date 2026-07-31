package com.babacar.secureauthservice.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Refresh token obligatoire")
        String refreshToken
) {}