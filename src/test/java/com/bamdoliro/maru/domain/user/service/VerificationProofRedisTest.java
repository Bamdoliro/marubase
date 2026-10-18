package com.bamdoliro.maru.domain.user.service;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerificationAttemptsExceededException;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
@ActiveProfiles("test")
@EnableConfigurationProperties(JwtProperties.class)
@Import(VerificationProofService.class)
class VerificationProofRedisTest {

    @Autowired
    private VerificationProofService proofs;
    @Autowired
    private StringRedisTemplate redis;

    @Test
    void 전화번호와_목적이_같은_증명만_소비한다() {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, VerificationType.SIGNUP);
        String proof = proofs.verify(phone, VerificationType.SIGNUP, code);
        assertEquals(43, proof.length());
        assertThrows(VerifyingHasFailedException.class,
                () -> proofs.consume(phone, VerificationType.UPDATE_PASSWORD, proof));
        assertThrows(VerifyingHasFailedException.class,
                () -> proofs.consume("another-" + phone, VerificationType.SIGNUP, proof));
        proofs.consume(phone, VerificationType.SIGNUP, proof);
        assertThrows(VerifyingHasFailedException.class,
                () -> proofs.consume(phone, VerificationType.SIGNUP, proof));
    }

    @Test
    void 다섯번째_실패에_폐기하고_정답으로도_복구하지_못한다() {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, VerificationType.UPDATE_PASSWORD);
        String wrong = code.equals("000000") ? "000001" : "000000";
        for (int i = 0; i < 4; i++) {
            assertThrows(VerificationCodeMismatchException.class,
                    () -> proofs.verify(phone, VerificationType.UPDATE_PASSWORD, wrong));
        }
        assertThrows(VerificationAttemptsExceededException.class,
                () -> proofs.verify(phone, VerificationType.UPDATE_PASSWORD, wrong));
        assertThrows(VerifyingHasFailedException.class,
                () -> proofs.verify(phone, VerificationType.UPDATE_PASSWORD, code));
    }

    @Test
    void 재발급하면_기존_증명을_무효화한다() {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, VerificationType.SIGNUP);
        String proof = proofs.verify(phone, VerificationType.SIGNUP, code);
        String nextCode = proofs.issue(phone, VerificationType.SIGNUP);
        assertThrows(VerifyingHasFailedException.class,
                () -> proofs.consume(phone, VerificationType.SIGNUP, proof));
        proofs.cancel(phone, VerificationType.SIGNUP, nextCode);
    }

    @Test
    void 인증번호와_증명에_TTL을_설정하고_만료되면_거부한다() {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, VerificationType.SIGNUP);
        String key = ReflectionTestUtils.invokeMethod(proofs, "key", phone, VerificationType.SIGNUP);
        try {
            assertTrue(redis.getExpire(key) > 0 && redis.getExpire(key) <= 300);
            assertNotEquals(code, redis.opsForHash().get(key, "code"));
            redis.expire(key, Duration.ZERO);
            assertThrows(VerifyingHasFailedException.class,
                    () -> proofs.verify(phone, VerificationType.SIGNUP, code));
            String next = proofs.issue(phone, VerificationType.SIGNUP);
            String proof = proofs.verify(phone, VerificationType.SIGNUP, next);
            assertTrue(redis.getExpire(key) > 0 && redis.getExpire(key) <= 300);
            assertNull(redis.opsForHash().get(key, "code"));
            assertNotEquals(proof, redis.opsForHash().get(key, "proof"));
            redis.expire(key, Duration.ZERO);
            assertThrows(VerifyingHasFailedException.class,
                    () -> proofs.consume(phone, VerificationType.SIGNUP, proof));
        } finally {
            redis.delete(key);
        }
    }

    @Test
    void 동시에_인증해도_증명은_하나만_발급한다() throws Exception {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, VerificationType.SIGNUP);
        try {
            assertEquals(1, concurrentSuccesses(() -> proofs.verify(phone, VerificationType.SIGNUP, code)));
        } finally {
            String key = ReflectionTestUtils.invokeMethod(proofs, "key", phone, VerificationType.SIGNUP);
            redis.delete(key);
        }
    }

    @Test
    void 동시에_소비해도_하나만_성공한다() throws Exception {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, VerificationType.UPDATE_PASSWORD);
        String proof = proofs.verify(phone, VerificationType.UPDATE_PASSWORD, code);
        assertEquals(1, concurrentSuccesses(() -> proofs.consume(phone, VerificationType.UPDATE_PASSWORD, proof)));
    }

    private long concurrentSuccesses(Runnable operation) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Boolean>> tasks = IntStream.range(0, 8).mapToObj(i -> (Callable<Boolean>) () -> {
                if (!start.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Test timed out");
                }
                try {
                    operation.run();
                    return true;
                } catch (VerifyingHasFailedException e) {
                    return false;
                }
            }).toList();
            var futures = tasks.stream().map(executor::submit).toList();
            start.countDown();
            long successes = 0;
            for (var future : futures) {
                if (future.get(5, TimeUnit.SECONDS)) {
                    successes++;
                }
            }
            return successes;
        }
    }
}
