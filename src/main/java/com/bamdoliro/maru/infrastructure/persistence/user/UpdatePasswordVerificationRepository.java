package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UpdatePasswordVerificationRepository extends CrudRepository<UpdatePasswordVerification, String>, VerificationRedisRepository {

    Optional<UpdatePasswordVerification> findById(String phoneNumber);
}
