package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.in.AuthenticateUserUseCase;
import com.babacar.secureauthservice.domain.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final AuthService authService;

    public AuthenticateUserUseCaseImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public String authenticate(String email, String password) {
        User user = authService.authenticate(email);
        return user.id().toString();
    }
}
