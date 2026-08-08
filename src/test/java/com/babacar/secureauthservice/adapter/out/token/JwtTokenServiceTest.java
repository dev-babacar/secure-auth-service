import com.babacar.secureauthservice.adapter.out.token.JwtTokenService;

//package com.babacar.secureauthservice.adapter.out.token;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();

        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();

        NimbusJwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableJWKSet<>(new JWKSet(rsaKey))
        );

        jwtTokenService = new JwtTokenService(encoder);
    }

    @Test
    @DisplayName("generateAccessToken : retourne un token non null")
    void should_generate_non_null_token() {
        String token = jwtTokenService.generateAccessToken(
                UUID.randomUUID().toString(),
                "test@example.com"
        );
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("generateAccessToken : token contient 3 parties JWT")
    void should_generate_valid_jwt_format() {
        String token = jwtTokenService.generateAccessToken(
                UUID.randomUUID().toString(),
                "test@example.com"
        );
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("generateAccessToken : deux tokens différents pour deux appels")
    void should_generate_unique_tokens() {
        String token1 = jwtTokenService.generateAccessToken(
                UUID.randomUUID().toString(), "user1@example.com"
        );
        String token2 = jwtTokenService.generateAccessToken(
                UUID.randomUUID().toString(), "user2@example.com"
        );
        assertThat(token1).isNotEqualTo(token2);
    }
}