package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.adapter.in.web.dto.MfaSetupResponse;
import com.babacar.secureauthservice.adapter.in.web.dto.MfaVerifyRequest;
import com.babacar.secureauthservice.adapter.out.totp.TotpAdapter;
import com.babacar.secureauthservice.domain.model.User;
import com.babacar.secureauthservice.domain.port.out.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth/mfa")
public class MfaController {

    private final TotpAdapter totpAdapter;
    private final UserRepository userRepository;

    public MfaController(TotpAdapter totpAdapter,
                         UserRepository userRepository) {
        this.totpAdapter = totpAdapter;
        this.userRepository = userRepository;
    }

    @PostMapping("/setup")
    public ResponseEntity<MfaSetupResponse> setup(
            @RequestParam String userId) {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable"
                ));

        String secret = totpAdapter.generateSecret();
        userRepository.updateMfaSecret(UUID.fromString(userId), secret);

        String qrCodeUrl = totpAdapter
                .generateQrCodeUrl(secret, user.email());

        return ResponseEntity.ok(
                new MfaSetupResponse(secret, qrCodeUrl)
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(
            @Valid @RequestBody MfaVerifyRequest request) {

        User user = userRepository
                .findById(UUID.fromString(request.userId()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable"
                ));

        if (user.mfaSecret() == null) {
            throw new IllegalArgumentException(
                    "MFA non configuré pour cet utilisateur"
            );
        }

        boolean valid = totpAdapter.verifyCode(
                user.mfaSecret(),
                request.code()
        );

        if (!valid) {
            throw new IllegalArgumentException("Code MFA invalide");
        }

        userRepository.enableMfa(UUID.fromString(request.userId()));
        return ResponseEntity.ok().build();
    }
}