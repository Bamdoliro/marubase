package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUuid(UUID uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.phoneNumber = :phoneNumber")
    Optional<User> findByPhoneNumberForUpdate(@Param("phoneNumber") String phoneNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.uuid = :uuid")
    Optional<User> findByUuidForUpdate(@Param("uuid") UUID uuid);
    boolean existsByPhoneNumber(String phoneNumber);
}
