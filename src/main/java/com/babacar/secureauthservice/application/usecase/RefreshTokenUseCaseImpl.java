package com.babacar.secureauthservice.application.usecase;

import com.babacar.secureauthservice.adapter.out.token.JwtTokenService;
import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.model.RefreshToken;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.in.RefreshTokenUseCase;
import com.babacar.secureauthservice.domain.port.out.UserRepository;
import com.babacar.secureauthservice.domain.service.TokenService;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final TokenService tokenService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public RefreshTokenUseCaseImpl(TokenService tokenService,
                                   JwtTokenService jwtTokenService,
                                   UserRepository userRepository) {
        this.tokenService = tokenService;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResult refresh(String rawRefreshToken) {
        String oldHash = hash(rawRefreshToken);

        // Génère un nouveau refresh token
        String newRawToken = UUID.randomUUID().toString();
        String newHash = hash(newRawToken);

        // Rotation — vérifie l'ancien, révoque, émet le nouveau
        // Si révoqué → TokenService révoque toute la famille
        RefreshToken newToken = tokenService.rotate(oldHash, newHash);

        // Récupère le user pour générer le JWT
        User user = userRepository.findById(newToken.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable"
                ));

        String accessToken = jwtTokenService
                .generateAccessToken(user.id().toString(), user.email());

        return new LoginResult(accessToken, newRawToken);
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