package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.domain.port.in.LogoutUseCase;
import com.babacar.secureauthservice.domain.port.out.AuditLogRepository;
import com.babacar.secureauthservice.domain.port.out.TokenBlacklist;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenBlacklist tokenBlacklist;
    private final JwtDecoder jwtDecoder;
    private final AuditLogRepository auditLogRepository;

    public LogoutUseCaseImpl(TokenBlacklist tokenBlacklist,
                             JwtDecoder jwtDecoder,
                             AuditLogRepository auditLogRepository) {
        this.tokenBlacklist = tokenBlacklist;
        this.jwtDecoder = jwtDecoder;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logout(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        String jti = jwt.getId();
        Instant expiresAt = jwt.getExpiresAt();

        if (jti == null || expiresAt == null) {
            throw new IllegalArgumentException("Token invalide");
        }

        Duration ttl = Duration.between(Instant.now(), expiresAt);

        if (!ttl.isNegative()) {
            tokenBlacklist.blacklist(jti, ttl);
        }

        // Audit log
        String subject = jwt.getSubject();
        if (subject != null) {
            try {
                auditLogRepository.log(
                        java.util.UUID.fromString(subject),
                        "LOGOUT",
                        null
                );
            } catch (Exception ignored) {}
        }
    }
}