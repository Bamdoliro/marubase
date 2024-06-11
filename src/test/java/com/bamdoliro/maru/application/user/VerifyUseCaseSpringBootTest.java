package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UpdatePasswordVerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
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
    private SignUpVerificationRepository signUpVerificationRepository;
    @Autowired
    private UpdatePasswordVerificationRepository updatePasswordVerificationRepository;

    @Test
    void 회원가입_인증이_업데이트_된다() {
        SignUpVerification signUpVerification = signUpVerificationRepository.save(new SignUpVerification("01012345678"));
        verifyUseCase.execute(new VerifyRequest(
                signUpVerification.getPhoneNumber(),
                signUpVerification.getCode(),
                VerificationType.SIGNUP
        ));

        SignUpVerification updatedSignUpVerification = signUpVerificationRepository.findById(signUpVerification.getPhoneNumber()).get();
        assertTrue(updatedSignUpVerification.getIsVerified());
    }

    @Test
    void 비밀번호_변경_인증이_업데이트_된다() {
        UpdatePasswordVerification verification = updatePasswordVerificationRepository.save(new UpdatePasswordVerification("01012345678"));
        verifyUseCase.execute(new VerifyRequest(
                verification.getPhoneNumber(),
                verification.getCode(),
                VerificationType.UPDATE_PASSWORD
        ));

        UpdatePasswordVerification updatedVerification = updatePasswordVerificationRepository.findById(verification.getPhoneNumber()).get();
        assertTrue(updatedVerification.getIsVerified());
    }
}