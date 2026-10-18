package com.bamdoliro.maru.domain.user.service;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerificationAttemptsExceededException;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
public class VerificationProofService {

    public static final long EXPIRES_IN_SECONDS = 300;
    private static final int MAX_FAILURES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final DefaultRedisScript<Long> ISSUE = new DefaultRedisScript<>("""
            redis.call('DEL', KEYS[1])
            redis.call('HSET', KEYS[1], 'code', ARGV[1], 'failures', 0)
            redis.call('EXPIRE', KEYS[1], ARGV[2])
            return 1
            """, Long.class);
    private static final DefaultRedisScript<Long> VERIFY = new DefaultRedisScript<>("""
            local code = redis.call('HGET', KEYS[1], 'code')
            if not code then return 0 end
            if code ~= ARGV[1] then
                local failures = redis.call('HINCRBY', KEYS[1], 'failures', 1)
                if failures >= tonumber(ARGV[4]) then
                    redis.call('DEL', KEYS[1])
                    return -2
                end
                return -1
            end
            redis.call('HDEL', KEYS[1], 'code', 'failures')
            redis.call('HSET', KEYS[1], 'proof', ARGV[2])
            redis.call('EXPIRE', KEYS[1], ARGV[3])
            return 1
            """, Long.class);
    private static final DefaultRedisScript<Long> CONSUME = new DefaultRedisScript<>("""
            if redis.call('HGET', KEYS[1], 'proof') ~= ARGV[1] then return 0 end
            redis.call('DEL', KEYS[1])
            return 1
            """, Long.class);
    private static final DefaultRedisScript<Long> CANCEL = new DefaultRedisScript<>("""
            if redis.call('HGET', KEYS[1], 'code') == ARGV[1] then
                return redis.call('DEL', KEYS[1])
            end
            return 0
            """, Long.class);

    private final StringRedisTemplate redisTemplate;
    private final byte[] secret;

    public VerificationProofService(StringRedisTemplate redisTemplate, JwtProperties properties) {
        this.redisTemplate = redisTemplate;
        this.secret = properties.getSecretKey().getBytes(StandardCharsets.UTF_8);
    }

    public String issue(String phoneNumber, VerificationType type) {
        String code = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
        requireResult(redisTemplate.execute(ISSUE, List.of(key(phoneNumber, type)),
                codeHash(phoneNumber, type, code), Long.toString(EXPIRES_IN_SECONDS)));
        return code;
    }

    public void cancel(String phoneNumber, VerificationType type, String code) {
        requireResult(redisTemplate.execute(CANCEL, List.of(key(phoneNumber, type)),
                codeHash(phoneNumber, type, code)));
    }

    public String verify(String phoneNumber, VerificationType type, String code) {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String proof = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        long result = requireResult(redisTemplate.execute(VERIFY, List.of(key(phoneNumber, type)),
                codeHash(phoneNumber, type, code), hash("proof:" + proof),
                Long.toString(EXPIRES_IN_SECONDS), Integer.toString(MAX_FAILURES)));
        if (result == -2) {
            throw new VerificationAttemptsExceededException();
        }
        if (result == -1) {
            throw new VerificationCodeMismatchException();
        }
        if (result != 1) {
            throw new VerifyingHasFailedException();
        }
        return proof;
    }

    public void consume(String phoneNumber, VerificationType type, String proof) {
        if (proof == null || !proof.matches("[A-Za-z0-9_-]{43}")) {
            throw new VerifyingHasFailedException();
        }
        long result = requireResult(redisTemplate.execute(CONSUME, List.of(key(phoneNumber, type)),
                hash("proof:" + proof)));
        if (result != 1) {
            throw new VerifyingHasFailedException();
        }
    }

    private String key(String phoneNumber, VerificationType type) {
        return "verification:v2:" + hash("subject:" + type.name() + ":" + phoneNumber);
    }

    private String codeHash(String phoneNumber, VerificationType type, String code) {
        return hash("code:" + type.name() + ":" + phoneNumber + ":" + code);
    }

    private long requireResult(Long result) {
        if (result == null) {
            throw new IllegalStateException("인증 상태를 확인하지 못했습니다.");
        }
        return result;
    }

    private String hash(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("인증 증명을 생성하지 못했습니다.", e);
        }
    }
}
