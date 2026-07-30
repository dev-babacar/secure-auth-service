package com.babacar.secureauthservice.config;

import com.babacar.secureauthservice.domain.port.out.AuditLogRepository;
import com.babacar.secureauthservice.domain.port.out.RefreshTokenRepository;
import com.babacar.secureauthservice.domain.port.out.UserRepository;
import com.babacar.secureauthservice.domain.service.AuthService;
import com.babacar.secureauthservice.domain.service.LoginService;
import com.babacar.secureauthservice.domain.service.MfaService;
import com.babacar.secureauthservice.domain.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthServiceConfig {

    @Bean
    public AuthService authService(UserRepository userRepository,
                                   AuditLogRepository auditLogRepository) {
        return new AuthService(userRepository, auditLogRepository);
    }

    @Bean
    public TokenService tokenService(RefreshTokenRepository refreshTokenRepository) {
        return new TokenService(refreshTokenRepository);
    }

    @Bean
    public MfaService mfaService() {
        return new MfaService();
    }

    @Bean
    public LoginService loginService(UserRepository userRepository,
                                     AuditLogRepository auditLogRepository) {
        return new LoginService(userRepository, auditLogRepository);
    }
}
