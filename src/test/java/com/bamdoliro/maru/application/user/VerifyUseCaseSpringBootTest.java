package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
@ActiveProfiles("test")
@EnableConfigurationProperties(JwtProperties.class)
@Import({VerifyUseCase.class, VerificationProofService.class})
class VerifyUseCaseSpringBootTest {

    @Autowired
    private VerifyUseCase verifyUseCase;
    @Autowired
    private VerificationProofService proofs;

    @ParameterizedTest
    @EnumSource(VerificationType.class)
    void 인증_성공_응답의_증명을_한번만_사용할_수_있다(VerificationType type) {
        String phone = UUID.randomUUID().toString();
        String code = proofs.issue(phone, type);
        var response = verifyUseCase.execute(new VerifyRequest(phone, code, type));
        proofs.consume(phone, type, response.getVerificationToken());
        assertThrows(VerifyingHasFailedException.class,
                () -> proofs.consume(phone, type, response.getVerificationToken()));
    }
}
