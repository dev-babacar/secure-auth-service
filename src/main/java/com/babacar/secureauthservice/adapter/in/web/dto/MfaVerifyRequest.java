package com.babacar.secureauthservice.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MfaVerifyRequest(
        @NotBlank(message = "UserId obligatoire")
        String userId,

        @NotBlank(message = "Code obligatoire")
        @Pattern(regexp = "\\d{6}", message = "Code doit être 6 chiffres")
        String code
) {}