package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegisterUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCaseImpl(authService, passwordEncoder);
    }

    @Test
    @DisplayName("register : encode le mot de passe avant de sauvegarder")
    void should_encode_password_before_saving() {
        String rawPassword = "MonMotDePasse123";
        String hashedPassword = "$2a$10$hashedpassword";

        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(authService.register(any(), any())).thenReturn(
                new User(UUID.randomUUID(), "test@example.com",
                        hashedPassword, Role.USER, false, null, false)
        );

        useCase.register("test@example.com", rawPassword);

        verify(passwordEncoder).encode(rawPassword);
        verify(authService).register(eq("test@example.com"), eq(hashedPassword));
    }

    @Test
    @DisplayName("register : ne sauvegarde jamais le mot de passe en clair")
    void should_never_save_plain_password() {
        String rawPassword = "MonMotDePasse123";
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$hash");
        when(authService.register(any(), any())).thenReturn(
                new User(UUID.randomUUID(), "test@example.com",
                        "$2a$10$hash", Role.USER, false, null, false)
        );

        useCase.register("test@example.com", rawPassword);

        verify(authService).register(any(), argThat(
                password -> !password.equals(rawPassword)
        ));
    }
}