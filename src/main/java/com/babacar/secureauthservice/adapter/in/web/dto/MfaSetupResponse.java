package com.babacar.secureauthservice.adapter.in.web.dto;

public record MfaSetupResponse(
        String secret,
        String qrCodeUrl
) {}