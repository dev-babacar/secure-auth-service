package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.adapter.out.token.JwtTokenService;
import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.in.LoginUseCase;
import com.babacar.secureauthservice.domain.service.LoginService;
import com.babacar.secureauthservice.domain.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private final LoginService loginService;
    private final TokenService tokenService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginUseCaseImpl(LoginService loginService,
                            TokenService tokenService,
                            JwtTokenService jwtTokenService,
                            PasswordEncoder passwordEncoder) {
        this.loginService = loginService;
        this.tokenService = tokenService;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResult login(String email, String password) {
        User user = loginService.findUserForLogin(email);

        if (!passwordEncoder.matches(password, user.passwordHash())) {
            loginService.logFailure(user);
            throw new IllegalArgumentException(
                    "Email ou mot de passe incorrect"
            );
        }

        loginService.logSuccess(user);

        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = hash(rawRefreshToken);
        tokenService.issue(user.id(), tokenHash);

        String accessToken = jwtTokenService
                .generateAccessToken(user.id().toString(), user.email());

        return new LoginResult(accessToken, rawRefreshToken);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}