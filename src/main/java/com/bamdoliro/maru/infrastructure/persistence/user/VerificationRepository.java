package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationRepository extends CrudRepository<Verification, String>, VerificationRedisRepository {

    Optional<Verification> findById(String phoneNumber);
}
