package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class SendVerificationUseCase {

    private final SendMessageService sendMessageService;
    private final VerificationRepository verificationRepository;

    public void execute(SendVerificationRequest request) {
        Verification verification = new Verification(request.getPhoneNumber());
        String text = String.format(
                "[부산소프트웨어마이스터고] 회원가입 인증번호는 [%s]입니다.",
                verification.getCode()
        );

        sendMessageService.execute(
                request.getPhoneNumber(),
                text
        );

        verificationRepository.save(verification);
    }
}
