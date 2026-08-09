package com.babacar.secureauthservice.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Token obligatoire")
        String token
) {}