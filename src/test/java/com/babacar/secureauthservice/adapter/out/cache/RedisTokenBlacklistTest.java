package com.babacar.secureauthservice.adapter.out.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisTokenBlacklistTest {

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Test
    @DisplayName("blacklist : stocke le token avec le bon préfixe et TTL")
    void should_blacklist_token_with_prefix() {
        when(redis.opsForValue()).thenReturn(valueOps);
        RedisTokenBlacklist blacklist = new RedisTokenBlacklist(redis);

        blacklist.blacklist("jti-123", Duration.ofMinutes(15));

        verify(valueOps).set(
                eq("blacklist:jti-123"),
                eq("revoked"),
                eq(Duration.ofMinutes(15))
        );
    }

    @Test
    @DisplayName("isBlacklisted : retourne true si token blacklisté")
    void should_return_true_when_blacklisted() {
        RedisTokenBlacklist blacklist = new RedisTokenBlacklist(redis);
        when(redis.hasKey("blacklist:jti-123")).thenReturn(true);

        assertThat(blacklist.isBlacklisted("jti-123")).isTrue();
    }

    @Test
    @DisplayName("isBlacklisted : retourne false si token non blacklisté")
    void should_return_false_when_not_blacklisted() {
        RedisTokenBlacklist blacklist = new RedisTokenBlacklist(redis);
        when(redis.hasKey("blacklist:unknown")).thenReturn(false);

        assertThat(blacklist.isBlacklisted("unknown")).isFalse();
    }
}