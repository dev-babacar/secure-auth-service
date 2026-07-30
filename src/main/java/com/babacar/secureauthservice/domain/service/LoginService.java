package com.babacar.secureauthservice.domain.service;

import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.out.AuditLogRepository;
import com.babacar.secureauthservice.domain.port.out.UserRepository;

public class LoginService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public LoginService(UserRepository userRepository,
                        AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public User findUserForLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Email ou mot de passe incorrect"
                ));
        auditLogRepository.log(user.id(), "LOGIN_ATTEMPT", null);
        return user;
    }

    public void logSuccess(User user) {
        auditLogRepository.log(user.id(), "LOGIN_SUCCESS", null);
    }

    public void logFailure(User user) {
        auditLogRepository.log(user.id(), "LOGIN_FAILED", null);
    }
}