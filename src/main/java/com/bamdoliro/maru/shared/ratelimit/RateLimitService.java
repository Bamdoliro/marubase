package com.bamdoliro.maru.shared.ratelimit;

import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final String KEY_VERSION = "v1";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final DefaultRedisScript<Long> INCREMENT_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('INCR', KEYS[1])
            if current == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            return current
            """, Long.class);

    private final StringRedisTemplate redisTemplate;
    private final byte[] hmacSecret;

    public RateLimitService(
            StringRedisTemplate redisTemplate,
            JwtProperties jwtProperties
    ) {
        this.redisTemplate = redisTemplate;
        this.hmacSecret = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
    }

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
        return "rate-limit:" + KEY_VERSION + ":" + policy.getKeyPrefix() + ":" + hash(identifier);
    }

    private String hash(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(hmacSecret, HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 알고리즘을 사용할 수 없습니다.", e);
        }
    }
}
