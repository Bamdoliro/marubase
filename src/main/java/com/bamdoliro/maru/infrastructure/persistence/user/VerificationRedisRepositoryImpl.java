package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VerificationRedisRepositoryImpl implements VerificationRedisRepository {

    private final RedisKeyValueTemplate template;

    @Override
    public void updateSignUpVerification(String phoneNumber, boolean verified) {
        PartialUpdate<SignUpVerification> update = new PartialUpdate<>(phoneNumber, SignUpVerification.class)
                .set("isVerified", verified)
                .refreshTtl(true);

        template.update(update);
    }

    @Override
    public void updatePasswordVerification(String phoneNumber, boolean verified) {
        PartialUpdate<UpdatePasswordVerification> update = new PartialUpdate<>(phoneNumber, UpdatePasswordVerification.class)
                .set("isVerified", verified)
                .refreshTtl(true);

        template.update(update);
    }
}
