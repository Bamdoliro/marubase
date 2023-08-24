package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface VerificationRepository extends KeyValueRepository<Verification, String> {

    Optional<Verification> findById(String phoneNumber);
}
