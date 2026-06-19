package com.babacar.secureauthservice.domain.port.in;

public interface ValidateMfaUseCase {
    boolean validate(String email, String code);
}
