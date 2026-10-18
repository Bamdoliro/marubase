package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
@ActiveProfiles("test")
@EnableConfigurationProperties(JwtProperties.class)
@Import(AuthSessionService.class)
class AuthSessionRedisTest {

    @Autowired
    private AuthSessionService sessions;

    @Test
    void 재로그인과_폐기_후_이전_세션은_사용할_수_없다() {
        UUID uuid = UUID.randomUUID();
        try {
            String first = sessions.create(uuid);
            sessions.validate(uuid, first);
            String second = sessions.create(uuid);
            assertThrows(ExpiredTokenException.class, () -> sessions.validate(uuid, first));
            sessions.validate(uuid, second);
            sessions.revoke(uuid);
            assertThrows(ExpiredTokenException.class, () -> sessions.validate(uuid, second));
        } finally {
            sessions.revoke(uuid);
        }
    }
}
