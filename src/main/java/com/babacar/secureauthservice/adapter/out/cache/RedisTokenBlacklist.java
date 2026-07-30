package com.babacar.secureauthservice.adapter.out.cache;

import com.babacar.secureauthservice.domain.port.out.TokenBlacklist;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisTokenBlacklist implements TokenBlacklist {

    private static final String PREFIX = "blacklist:";
    private final StringRedisTemplate redis;

    public RedisTokenBlacklist(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void blacklist(String jti, Duration ttl) {
        redis.opsForValue().set(PREFIX + jti, "revoked", ttl);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + jti));
    }
}