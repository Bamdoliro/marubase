package com.bamdoliro.maru.infrastructure.persistence.user;

public interface VerificationRedisRepository {

    void updateVerification(String phoneNumber, boolean verified);
}
