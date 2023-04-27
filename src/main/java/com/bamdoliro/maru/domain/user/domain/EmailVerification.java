package com.bamdoliro.maru.domain.user.domain;

import com.bamdoliro.maru.shared.util.RandomCodeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "emailVerification", timeToLive = 60 * 5)
public class EmailVerification {

    @Id
    private String email;

    private String code;

    public EmailVerification(String email) {
        this.email = email;
        this.code = RandomCodeUtil.generate(6);
    }
}
