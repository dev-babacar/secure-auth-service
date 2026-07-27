package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.domain.port.in.RegisterUserUseCase;
import com.babacar.secureauthservice.domain.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCaseImpl(AuthService authService,
                                   PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(String email, String password) {
        String hash = passwordEncoder.encode(password);
        authService.register(email, hash);
    }
}
