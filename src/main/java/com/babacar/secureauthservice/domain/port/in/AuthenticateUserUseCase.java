package com.babacar.secureauthservice.domain.port.in;

public interface AuthenticateUserUseCase {
    String authenticate(String email, String password);
}
