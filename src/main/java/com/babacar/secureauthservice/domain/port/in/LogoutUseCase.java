package com.babacar.secureauthservice.domain.port.in;

public interface LogoutUseCase {
    void logout(String token);
}