package com.babacar.secureauthservice.domain.service;

import com.babacar.secureauthservice.domain.model.Role;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.out.AuditLogRepository;
import com.babacar.secureauthservice.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, auditLogRepository);
    }

    // ─── register() ───

    @Test
    @DisplayName("register : crée un user avec role USER par défaut")
    void should_register_user_with_default_role() {
        // GIVEN
        String email = "test@example.com";
        String hash = "$2a$10$hashedpassword";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(
                inv -> inv.getArgument(0)
        );

        // WHEN
        User result = authService.register(email, hash);

        // THEN
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.mfaEnabled()).isFalse();
        assertThat(result.id()).isNotNull();
    }

    @Test
    @DisplayName("register : lève une exception si email déjà utilisé")
    void should_throw_when_email_already_registered() {
        // GIVEN
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(email, "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("register : audit log REGISTER_SUCCESS enregistré")
    void should_log_register_success() {
        // GIVEN
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(
                inv -> inv.getArgument(0)
        );

        // WHEN
        authService.register(email, "hash");

        // THEN
        verify(auditLogRepository, times(1))
                .log(any(UUID.class), eq("REGISTER_SUCCESS"), isNull());
    }

    @Test
    @DisplayName("register : ne sauvegarde pas si email déjà utilisé")
    void should_not_save_when_email_exists() {
        // GIVEN
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // WHEN
        assertThatThrownBy(() ->
                authService.register("exists@example.com", "hash")
        );

        // THEN
        verify(userRepository, never()).save(any());
    }

    // ─── authenticate() ───

    @Test
    @DisplayName("authenticate : retourne le user si email trouvé")
    void should_return_user_when_email_found() {
        // GIVEN
        String email = "test@example.com";
        User user = new User(
                UUID.randomUUID(), email, "hash",
                Role.USER, false, null, false
        );
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // WHEN
        User result = authService.authenticate(email);

        // THEN
        assertThat(result.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("authenticate : lève une exception si email introuvable")
    void should_throw_when_user_not_found() {
        // GIVEN
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() ->
                authService.authenticate("unknown@example.com")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}