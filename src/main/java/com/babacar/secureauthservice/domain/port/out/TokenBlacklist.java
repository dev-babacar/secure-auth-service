package com.babacar.secureauthservice.domain.port.out;

import java.time.Duration;

public interface TokenBlacklist {
    void blacklist(String jti, Duration ttl);
    boolean isBlacklisted(String jti);
}