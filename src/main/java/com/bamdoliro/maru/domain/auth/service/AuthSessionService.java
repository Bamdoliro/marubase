package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private static final String PREFIX = "auth-session:v1:";
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    public String create(UUID userUuid) {
        String sessionId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(PREFIX + userUuid, sessionId,
                Duration.ofMillis(jwtProperties.getRefreshExpirationTime()));
        return sessionId;
    }

    public void validate(UUID userUuid, String sessionId) {
        if (sessionId == null || !sessionId.equals(redisTemplate.opsForValue().get(PREFIX + userUuid))) {
            throw new ExpiredTokenException();
        }
    }

    public void revoke(UUID userUuid) {
        redisTemplate.delete(PREFIX + userUuid);
    }
}
