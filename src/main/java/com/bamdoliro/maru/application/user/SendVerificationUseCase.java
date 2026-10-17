package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UpdatePasswordVerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.ratelimit.RateLimitPolicy;
import com.bamdoliro.maru.shared.ratelimit.RateLimitService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class SendVerificationUseCase {

    private final SendMessageService sendMessageService;
    private final SignUpVerificationRepository signUpVerificationRepository;
    private final UpdatePasswordVerificationRepository updatePasswordVerificationRepository;
    private final RateLimitService rateLimitService;

    public void execute(SendVerificationRequest request) {

        if (request.getType() == VerificationType.SIGNUP) {
            SignUpVerification signUpVerification = new SignUpVerification(request.getPhoneNumber());
            String text = String.format(
                    "[부산소프트웨어마이스터고] 회원가입 인증번호는 [%s]입니다.",
                    signUpVerification.getCode()
            );

            sendMessageService.execute(
                    request.getPhoneNumber(),
                    text
            );

            signUpVerificationRepository.save(signUpVerification);
            resetVerificationFailure(request);
        } else {
            UpdatePasswordVerification updatePasswordVerification = new UpdatePasswordVerification(request.getPhoneNumber());
            String text = String.format(
                    "[부산소프트웨어마이스터고] 비밀번호 변경 인증번호는 [%s]입니다.",
                    updatePasswordVerification.getCode()
            );

            sendMessageService.execute(
                    request.getPhoneNumber(),
                    text
            );

            updatePasswordVerificationRepository.save(updatePasswordVerification);
            resetVerificationFailure(request);
        }
    }

    private void resetVerificationFailure(SendVerificationRequest request) {
        rateLimitService.reset(
                RateLimitPolicy.VERIFICATION_FAILURE,
                request.getType().name() + ":" + request.getPhoneNumber()
        );
    }
}
