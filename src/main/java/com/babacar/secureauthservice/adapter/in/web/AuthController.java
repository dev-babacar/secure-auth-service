package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.adapter.in.web.dto.LoginRequest;
import com.babacar.secureauthservice.adapter.in.web.dto.RefreshRequest;
import com.babacar.secureauthservice.adapter.in.web.dto.RegisterRequest;
import com.babacar.secureauthservice.adapter.in.web.dto.TokenResponse;
import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.port.in.LoginUseCase;
import com.babacar.secureauthservice.domain.port.in.RefreshTokenUseCase;
import com.babacar.secureauthservice.domain.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.register(
                request.email(),
                request.password()
        );
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.login(
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(
                TokenResponse.of(result.accessToken(), result.refreshToken())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {
        LoginResult result = refreshTokenUseCase
                .refresh(request.refreshToken());
        return ResponseEntity.ok(
                TokenResponse.of(result.accessToken(), result.refreshToken())
        );
    }
}
