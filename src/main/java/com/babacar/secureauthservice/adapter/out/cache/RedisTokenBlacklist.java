package com.babacar.secureauthservice.adapter.out.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class RedisTokenBlacklist {

    private static final String PREFIX = "blacklist:";
    private final StringRedisTemplate redis;

    public RedisTokenBlacklist(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void blacklist(String jti, Duration ttl) {
        redis.opsForValue().set(PREFIX + jti, "revoked", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + jti));
    }
}
