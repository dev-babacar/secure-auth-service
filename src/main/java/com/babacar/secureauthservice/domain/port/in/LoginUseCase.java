package com.babacar.secureauthservice.domain.port.in;

import com.babacar.secureauthservice.domain.model.LoginResult;

public interface LoginUseCase {
    LoginResult login(String email, String password);
}