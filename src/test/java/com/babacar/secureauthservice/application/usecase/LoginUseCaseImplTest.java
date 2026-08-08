package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.adapter.out.token.JwtTokenService;
import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.service.LoginService;
import com.babacar.secureauthservice.domain.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock private LoginService loginService;
    @Mock private TokenService tokenService;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private PasswordEncoder passwordEncoder;

    private LoginUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCaseImpl(
                loginService, tokenService,
                jwtTokenService, passwordEncoder
        );
    }

    @Test
    @DisplayName("login : retourne access et refresh token si credentials valides")
    void should_return_tokens_on_valid_credentials() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId, "test@example.com", "$2a$10$hash",
                Role.USER, false, null, false
        );
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(), "hash", userId,
                Instant.now().plusSeconds(3600), false, UUID.randomUUID()
        );

        when(loginService.findUserForLogin("test@example.com"))
                .thenReturn(user);
        when(passwordEncoder.matches("password", "$2a$10$hash"))
                .thenReturn(true);
        when(tokenService.issue(any(), any())).thenReturn(refreshToken);
        when(jwtTokenService.generateAccessToken(any(), any()))
                .thenReturn("access-token");

        LoginResult result = useCase.login("test@example.com", "password");

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("login : lève exception si mot de passe incorrect")
    void should_throw_on_invalid_password() {
        User user = new User(
                UUID.randomUUID(), "test@example.com", "$2a$10$hash",
                Role.USER, false, null, false
        );

        when(loginService.findUserForLogin(any())).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() ->
                useCase.login("test@example.com", "wrongpassword")
        ).isInstanceOf(IllegalArgumentException.class);

        verify(loginService).logFailure(user);
    }

    @Test
    @DisplayName("login : enregistre LOGIN_SUCCESS si credentials valides")
    void should_log_success_on_valid_login() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId, "test@example.com", "$2a$10$hash",
                Role.USER, false, null, false
        );
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(), "hash", userId,
                Instant.now().plusSeconds(3600), false, UUID.randomUUID()
        );

        when(loginService.findUserForLogin(any())).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(tokenService.issue(any(), any())).thenReturn(refreshToken);
        when(jwtTokenService.generateAccessToken(any(), any()))
                .thenReturn("token");

        useCase.login("test@example.com", "password");

        verify(loginService).logSuccess(user);
    }
}