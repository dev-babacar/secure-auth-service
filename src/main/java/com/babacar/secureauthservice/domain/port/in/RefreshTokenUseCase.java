package com.babacar.secureauthservice.domain.port.in;

import com.babacar.secureauthservice.domain.model.LoginResult;

public interface RefreshTokenUseCase {
    LoginResult refresh(String rawRefreshToken);
}