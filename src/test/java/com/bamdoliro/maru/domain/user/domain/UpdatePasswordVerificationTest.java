package com.bamdoliro.maru.domain.user.domain;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisHash;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdatePasswordVerificationTest {

    @Test
    void 비밀번호_변경_인증은_5분_후_만료된다() {
        // given
        RedisHash redisHash = UpdatePasswordVerification.class.getAnnotation(RedisHash.class);

        // when and then
        assertEquals(60 * 5, redisHash.timeToLive());
    }
}
