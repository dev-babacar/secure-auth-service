package com.babacar.secureauthservice.domain.port.in;

public interface RegisterUserUseCase {
    void register(String email, String password);
}
