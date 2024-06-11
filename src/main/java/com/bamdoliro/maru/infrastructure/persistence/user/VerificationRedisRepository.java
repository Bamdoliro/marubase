package com.bamdoliro.maru.infrastructure.persistence.user;

public interface VerificationRedisRepository {

    void updateSignUpVerification(String phoneNumber, boolean verified);
    void updatePasswordVerification(String phoneNumber, boolean verified);
}
