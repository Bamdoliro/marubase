package com.bamdoliro.maru.domain.user.service;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationProofServiceTest {

    @Test
    void Redis_결과가_없으면_인증과_소비를_허용하지_않는다() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        JwtProperties properties = mock(JwtProperties.class);
        when(properties.getSecretKey()).thenReturn("test-secret");
        VerificationProofService proofs = new VerificationProofService(redis, properties);
        assertThrows(IllegalStateException.class, () -> proofs.issue("test", VerificationType.SIGNUP));
        assertThrows(IllegalStateException.class, () -> proofs.verify("test", VerificationType.SIGNUP, "123456"));
        assertThrows(IllegalStateException.class, () -> proofs.consume("test", VerificationType.SIGNUP, "a".repeat(43)));
        assertThrows(VerifyingHasFailedException.class, () -> proofs.consume("test", VerificationType.SIGNUP, null));
    }
}
