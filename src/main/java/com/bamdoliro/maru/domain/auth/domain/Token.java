package com.bamdoliro.maru.domain.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 15)
public class Token {

    @Id
    private String email;

    private String token;

    @Builder
    public Token(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
