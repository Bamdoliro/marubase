package com.bamdoliro.maru.shared.ratelimit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final DefaultRedisScript<Long> INCREMENT_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('INCR', KEYS[1])
            if current == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            return current
            """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public long consume(RateLimitPolicy policy, String identifier) {
        long requestCount = increment(policy, identifier);
        if (requestCount > policy.getMaxRequests()) {
            throw new RateLimitExceededException();
        }
        return requestCount;
    }

    public long increment(RateLimitPolicy policy, String identifier) {
        Long requestCount = redisTemplate.execute(
                INCREMENT_SCRIPT,
                List.of(createKey(policy, identifier)),
                Long.toString(policy.getWindow().toSeconds())
        );
        if (requestCount == null) {
            throw new IllegalStateException("요청 제한 횟수를 저장하지 못했습니다.");
        }
        return requestCount;
    }

    public void reset(RateLimitPolicy policy, String identifier) {
        redisTemplate.delete(createKey(policy, identifier));
    }

    private String createKey(RateLimitPolicy policy, String identifier) {
        return "rate-limit:" + policy.getKeyPrefix() + ":" + hash(identifier);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }
}
