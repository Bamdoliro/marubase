package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SignUpVerificationRepository extends CrudRepository<SignUpVerification, String>, VerificationRedisRepository {

    Optional<SignUpVerification> findById(String phoneNumber);
}
