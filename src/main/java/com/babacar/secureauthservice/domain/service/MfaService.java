package com.babacar.secureauthservice.domain.service;

import com.babacar.secureauthservice.domain.model.MfaSecret;

public class MfaService {

    public MfaSecret generateSecret() {
        // la génération réelle se fera dans l'adapter
        // ici on valide juste la règle métier
        String raw = java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
        return new MfaSecret(raw);
    }

    public boolean verify(MfaSecret secret, String code) {
        // règle métier : code à 6 chiffres uniquement
        if (code == null || !code.matches("\\d{6}")) {
            return false;
        }
        // la vérification TOTP réelle se fait dans l'adapter
        // le domaine exprime juste la règle de format
        return true;
    }
}
