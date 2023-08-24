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
    private String uuid;

    private String token;

    @Builder
    public Token(String uuid, String token) {
        this.uuid = uuid;
        this.token = token;
    }
}
