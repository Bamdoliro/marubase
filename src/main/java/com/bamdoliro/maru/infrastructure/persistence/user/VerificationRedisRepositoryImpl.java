package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VerificationRedisRepositoryImpl implements VerificationRedisRepository {

    private final RedisKeyValueTemplate template;

    @Override
    public void updateVerification(String phoneNumber, boolean verified) {
        PartialUpdate<Verification> update = new PartialUpdate<Verification>(phoneNumber, Verification.class)
                .set("isVerified", verified);

        template.update(update);
    }
}
