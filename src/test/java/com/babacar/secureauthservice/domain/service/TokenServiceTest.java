package com.babacar.secureauthservice.domain.service;

import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.port.out.RefreshTokenRepository;
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
class TokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(refreshTokenRepository);
    }

    // ─── issue() ───

    @Test
    @DisplayName("issue : crée un refresh token valide 7 jours")
    void should_issue_token_with_7_days_expiry() {
        // GIVEN
        UUID userId = UUID.randomUUID();
        String hash = "hashedtoken";
        when(refreshTokenRepository.save(any())).thenAnswer(
                inv -> inv.getArgument(0)
        );

        // WHEN
        RefreshToken token = tokenService.issue(userId, hash);

        // THEN
        assertThat(token.userId()).isEqualTo(userId);
        assertThat(token.tokenHash()).isEqualTo(hash);
        assertThat(token.revoked()).isFalse();
        assertThat(token.expiresAt())
                .isAfter(Instant.now().plusSeconds(6 * 24 * 3600));
        assertThat(token.family()).isNotNull();
    }

    @Test
    @DisplayName("issue : génère une nouvelle famille à chaque émission")
    void should_generate_new_family_on_issue() {
        // GIVEN
        when(refreshTokenRepository.save(any())).thenAnswer(
                inv -> inv.getArgument(0)
        );

        // WHEN
        RefreshToken token1 = tokenService.issue(UUID.randomUUID(), "hash1");
        RefreshToken token2 = tokenService.issue(UUID.randomUUID(), "hash2");

        // THEN
        assertThat(token1.family()).isNotEqualTo(token2.family());
    }

    // ─── rotate() ───

    @Test
    @DisplayName("rotate : génère un nouveau token avec la même famille")
    void should_rotate_and_keep_same_family() {
        // GIVEN
        UUID family = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RefreshToken oldToken = new RefreshToken(
                UUID.randomUUID(), "oldhash", userId,
                Instant.now().plusSeconds(3600), false, family
        );
        when(refreshTokenRepository.findByTokenHash("oldhash"))
                .thenReturn(Optional.of(oldToken));
        when(refreshTokenRepository.save(any())).thenAnswer(
                inv -> inv.getArgument(0)
        );

        // WHEN
        RefreshToken newToken = tokenService.rotate("oldhash", "newhash");

        // THEN
        assertThat(newToken.family()).isEqualTo(family);
        assertThat(newToken.tokenHash()).isEqualTo("newhash");
        assertThat(newToken.revoked()).isFalse();
    }

    @Test
    @DisplayName("rotate : révoque toute la famille si token déjà révoqué")
    void should_revoke_family_when_token_reused() {
        // GIVEN
        UUID family = UUID.randomUUID();
        RefreshToken revokedToken = new RefreshToken(
                UUID.randomUUID(), "revokedhash", UUID.randomUUID(),
                Instant.now().plusSeconds(3600), true, family
        );
        when(refreshTokenRepository.findByTokenHash("revokedhash"))
                .thenReturn(Optional.of(revokedToken));

        // WHEN / THEN
        assertThatThrownBy(() ->
                tokenService.rotate("revokedhash", "newhash")
        )
                .isInstanceOf(IllegalStateException.class);

        verify(refreshTokenRepository, times(1))
                .revokeAllByFamily(family);
    }

    @Test
    @DisplayName("rotate : lève une exception si token introuvable")
    void should_throw_when_token_not_found() {
        // GIVEN
        when(refreshTokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() ->
                tokenService.rotate("unknown", "newhash")
        )
                .isInstanceOf(IllegalArgumentException.class);
    }
}