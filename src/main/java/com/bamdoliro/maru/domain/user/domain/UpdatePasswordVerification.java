package com.bamdoliro.maru.domain.user.domain;

import com.bamdoliro.maru.shared.util.RandomCodeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "password-verification")
public class UpdatePasswordVerification {

    @Id
    private String phoneNumber;

    private String code;

    private Boolean isVerified;

    public UpdatePasswordVerification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.code = RandomCodeUtil.generate(6);
        this.isVerified = false;
    }

    public void verify() {
        this.isVerified = true;
    }
}
