package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthSessionServiceTest {

    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final JwtProperties properties = mock(JwtProperties.class);
    private final AuthSessionService sessions = new AuthSessionService(redis, properties);

    @Test
    @SuppressWarnings("unchecked")
    void 세션을_만료시간과_함께_저장한다() {
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        when(properties.getRefreshExpirationTime()).thenReturn(60000L);
        UUID uuid = UUID.randomUUID();
        String session = sessions.create(uuid);
        assertDoesNotThrow(() -> UUID.fromString(session));
        verify(values).set("auth-session:v1:" + uuid, session, Duration.ofMinutes(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    void 세션이_없거나_교체됐으면_거부한다() {
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        UUID uuid = UUID.randomUUID();
        when(values.get("auth-session:v1:" + uuid)).thenReturn(null, "new-session", "same-session");
        assertThrows(ExpiredTokenException.class, () -> sessions.validate(uuid, "old-session"));
        assertThrows(ExpiredTokenException.class, () -> sessions.validate(uuid, "old-session"));
        assertDoesNotThrow(() -> sessions.validate(uuid, "same-session"));
    }

    @Test
    void 세션_폐기_실패를_숨기지_않는다() {
        UUID uuid = UUID.randomUUID();
        doThrow(new IllegalStateException("Redis unavailable")).when(redis).delete("auth-session:v1:" + uuid);
        assertThrows(IllegalStateException.class, () -> sessions.revoke(uuid));
    }
}
