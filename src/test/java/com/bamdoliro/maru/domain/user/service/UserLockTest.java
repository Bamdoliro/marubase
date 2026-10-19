package com.bamdoliro.maru.domain.user.service;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserFacade.class, UserLockTest.EncryptionConfig.class, com.bamdoliro.maru.shared.config.JpaConfig.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserLockTest {

    @Autowired
    private UserFacade userFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void 비밀번호_변경_중인_계정의_로그인은_커밋된_새_비밀번호를_조회한다() throws Exception {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        User saved = transaction.execute(status -> userRepository.saveAndFlush(UserFixture.createUser()));
        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch reading = new CountDownLatch(1);
        CountDownLatch read = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var reset = executor.submit(() -> transaction.executeWithoutResult(status -> {
                User user = userFacade.getUserForUpdate(saved.getPhoneNumber());
                user.updatePassword("changed-password");
                locked.countDown();
                await(release);
            }));
            try {
                assertTrue(locked.await(5, TimeUnit.SECONDS));
                var login = executor.submit(() -> transaction.execute(status -> {
                    reading.countDown();
                    User user = userFacade.getUserForUpdate(saved.getPhoneNumber());
                    read.countDown();
                    return user.getPassword().match("changed-password");
                }));
                assertTrue(reading.await(5, TimeUnit.SECONDS));
                assertFalse(read.await(200, TimeUnit.MILLISECONDS));
                release.countDown();
                reset.get(5, TimeUnit.SECONDS);
                assertTrue(login.get(5, TimeUnit.SECONDS));
            } finally {
                release.countDown();
            }
        } finally {
            transaction.executeWithoutResult(status -> userRepository.deleteById(saved.getId()));
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Test timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    @TestConfiguration
    static class EncryptionConfig {
        @Bean
        com.querydsl.jpa.impl.JPAQueryFactory queryFactory(jakarta.persistence.EntityManager entityManager) {
            return new com.querydsl.jpa.impl.JPAQueryFactory(entityManager);
        }

        @Bean
        StringEncryptor stringEncryptor() {
            return new StringEncryptor() {
                public String encrypt(String value) { return value; }
                public String decrypt(String value) { return value; }
            };
        }
    }
}
