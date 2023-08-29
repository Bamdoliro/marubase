package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class VerifyUseCaseSpringBootTest {

    @Autowired
    private VerifyUseCase verifyUseCase;

    @Autowired
    private VerificationRepository verificationRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void 인증이_업데이트_된다() {
        Verification verification = verificationRepository.save(new Verification("01012345678"));
        verifyUseCase.execute(new VerifyRequest(
                verification.getPhoneNumber(),
                verification.getCode()
        ));

        Verification updatedVerification = verificationRepository.findById(verification.getPhoneNumber()).get();
        assertTrue(updatedVerification.getIsVerified());
    }
}