package com.babacar.secureauthservice.adapter.out.totp;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.*;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Component;

@Component
public class TotpAdapter {

    private final DefaultSecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;

    public TotpAdapter() {
        this.secretGenerator = new DefaultSecretGenerator();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        this.codeVerifier = new DefaultCodeVerifier(
                codeGenerator,
                new SystemTimeProvider()
        );
    }

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCodeUrl(String secret, String email) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                "AuthService",
                email,
                secret,
                "AuthService"
        );
    }

    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}