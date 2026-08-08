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
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(userRepository, auditLogRepository);
    }

    @Test
    @DisplayName("findUserForLogin : retourne le user si email trouvé")
    void should_return_user_when_email_found() {
        User user = new User(
                UUID.randomUUID(), "test@example.com", "hash",
                Role.USER, false, null, false
        );
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        User result = loginService.findUserForLogin("test@example.com");

        assertThat(result.email()).isEqualTo("test@example.com");
        verify(auditLogRepository).log(any(), eq("LOGIN_ATTEMPT"), isNull());
    }

    @Test
    @DisplayName("findUserForLogin : lève exception si email introuvable")
    void should_throw_when_email_not_found() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                loginService.findUserForLogin("unknown@example.com")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("logSuccess : enregistre LOGIN_SUCCESS")
    void should_log_success() {
        User user = new User(
                UUID.randomUUID(), "test@example.com", "hash",
                Role.USER, false, null, false
        );

        loginService.logSuccess(user);

        verify(auditLogRepository).log(
                eq(user.id()), eq("LOGIN_SUCCESS"), isNull()
        );
    }

    @Test
    @DisplayName("logFailure : enregistre LOGIN_FAILED")
    void should_log_failure() {
        User user = new User(
                UUID.randomUUID(), "test@example.com", "hash",
                Role.USER, false, null, false
        );

        loginService.logFailure(user);

        verify(auditLogRepository).log(
                eq(user.id()), eq("LOGIN_FAILED"), isNull()
        );
    }
}