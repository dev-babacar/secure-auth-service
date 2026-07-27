package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.adapter.in.web.dto.RegisterRequest;
import com.babacar.secureauthservice.domain.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
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
}
