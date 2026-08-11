package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.adapter.in.web.dto.*;
import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.port.in.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock private RegisterUserUseCase registerUserUseCase;
    @Mock private LoginUseCase loginUseCase;
    @Mock private RefreshTokenUseCase refreshTokenUseCase;
    @Mock private LogoutUseCase logoutUseCase;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(
                registerUserUseCase,
                loginUseCase,
                refreshTokenUseCase,
                logoutUseCase
        );
    }

    @Test
    @DisplayName("register : retourne 201 si données valides")
    void should_return_201_on_valid_register() {
        doNothing().when(registerUserUseCase).register(any(), any());

        ResponseEntity<Void> response = controller.register(
                new RegisterRequest("test@example.com", "MonMotDePasse123")
        );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("login : retourne 200 avec tokens")
    void should_return_200_with_tokens_on_valid_login() {
        when(loginUseCase.login(any(), any()))
                .thenReturn(new LoginResult("access-token", "refresh-token"));

        ResponseEntity<TokenResponse> response = controller.login(
                new LoginRequest("test@example.com", "MonMotDePasse123")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken())
                .isEqualTo("access-token");
    }

    @Test
    @DisplayName("refresh : retourne 200 avec nouveaux tokens")
    void should_return_200_on_valid_refresh() {
        when(refreshTokenUseCase.refresh(any()))
                .thenReturn(new LoginResult("new-access", "new-refresh"));

        ResponseEntity<TokenResponse> response = controller.refresh(
                new RefreshRequest("some-token")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken())
                .isEqualTo("new-access");
    }

    @Test
    @DisplayName("logout : retourne 204")
    void should_return_204_on_logout() {
        doNothing().when(logoutUseCase).logout(any());

        ResponseEntity<Void> response = controller.logout(
                new LogoutRequest("some-token")
        );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("register : lève exception si email déjà utilisé")
    void should_throw_when_email_already_registered() {
        doThrow(new IllegalArgumentException("Email already registered"))
                .when(registerUserUseCase).register(any(), any());

        assertThatThrownBy(() ->
                controller.register(
                        new RegisterRequest("test@example.com", "MonMotDePasse123")
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }
}