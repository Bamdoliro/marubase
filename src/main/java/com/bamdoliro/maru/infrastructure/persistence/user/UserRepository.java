package com.bamdoliro.maru.infrastructure.persistence.user;

import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUuid(UUID uuid);
    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from User u where u.isTestData = true")
    int deleteAllOfTestData();
}
