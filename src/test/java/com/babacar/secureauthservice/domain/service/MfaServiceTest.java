package com.babacar.secureauthservice.domain.service;

import com.babacar.secureauthservice.domain.model.MfaSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MfaServiceTest {

    private MfaService mfaService;

    @BeforeEach
    void setUp() {
        mfaService = new MfaService();
    }

    @Test
    @DisplayName("generateSecret : retourne un secret non null et non vide")
    void should_generate_non_null_secret() {
        MfaSecret secret = mfaService.generateSecret();

        assertThat(secret).isNotNull();
        assertThat(secret.value()).isNotBlank();
    }

    @Test
    @DisplayName("generateSecret : retourne un secret de 16 caractères")
    void should_generate_16_char_secret() {
        MfaSecret secret = mfaService.generateSecret();

        assertThat(secret.value()).hasSize(16);
    }

    @Test
    @DisplayName("verify : retourne false si code null")
    void should_return_false_when_code_null() {
        MfaSecret secret = new MfaSecret("ABCDEFGHIJKLMNOP");

        boolean result = mfaService.verify(secret, null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("verify : retourne false si code pas 6 chiffres")
    void should_return_false_when_code_not_6_digits() {
        MfaSecret secret = new MfaSecret("ABCDEFGHIJKLMNOP");

        assertThat(mfaService.verify(secret, "123")).isFalse();
        assertThat(mfaService.verify(secret, "abcdef")).isFalse();
        assertThat(mfaService.verify(secret, "1234567")).isFalse();
    }

    @Test
    @DisplayName("verify : retourne true si code est 6 chiffres valides")
    void should_return_true_when_code_is_6_digits() {
        MfaSecret secret = new MfaSecret("ABCDEFGHIJKLMNOP");

        boolean result = mfaService.verify(secret, "123456");

        assertThat(result).isTrue();
    }
}