package com.bamdoliro.maru.domain.user.service;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@RequiredArgsConstructor
@Component
public class UserFacade {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.MANDATORY)
    public User getUserForUpdate(String phoneNumber) {
        return userRepository.findByPhoneNumberForUpdate(phoneNumber)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public User getUserByUuidForUpdate(UUID uuid) {
        User user = userRepository.findByUuidForUpdate(uuid)
                .orElseThrow(UserNotFoundException::new);
        entityManager.refresh(user, LockModeType.PESSIMISTIC_WRITE);
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public User getUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(UserNotFoundException::new);
    }
}
