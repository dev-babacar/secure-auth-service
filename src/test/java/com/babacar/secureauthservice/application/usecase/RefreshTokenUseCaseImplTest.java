package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.adapter.out.token.JwtTokenService;
import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.out.UserRepository;
import com.babacar.secureauthservice.domain.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseImplTest {

    @Mock private TokenService tokenService;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private UserRepository userRepository;

    private RefreshTokenUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RefreshTokenUseCaseImpl(
                tokenService, jwtTokenService, userRepository
        );
    }

    @Test
    @DisplayName("refresh : retourne nouveaux tokens si refresh token valide")
    void should_return_new_tokens_on_valid_refresh() {
        UUID userId = UUID.randomUUID();
        RefreshToken newToken = new RefreshToken(
                UUID.randomUUID(), "newhash", userId,
                Instant.now().plusSeconds(3600), false, UUID.randomUUID()
        );
        User user = new User(
                userId, "test@example.com", "hash",
                Role.USER, false, null, false
        );

        when(tokenService.rotate(any(), any())).thenReturn(newToken);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenService.generateAccessToken(any(), any()))
                .thenReturn("new-access-token");

        LoginResult result = useCase.refresh("raw-refresh-token");

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("refresh : lève exception si user introuvable après rotation")
    void should_throw_when_user_not_found_after_rotation() {
        UUID userId = UUID.randomUUID();
        RefreshToken newToken = new RefreshToken(
                UUID.randomUUID(), "newhash", userId,
                Instant.now().plusSeconds(3600), false, UUID.randomUUID()
        );

        when(tokenService.rotate(any(), any())).thenReturn(newToken);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.refresh("raw-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}