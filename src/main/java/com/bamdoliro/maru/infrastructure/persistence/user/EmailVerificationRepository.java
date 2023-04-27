package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends KeyValueRepository<EmailVerification, String> {

    Optional<EmailVerification> findById(String email);
}
