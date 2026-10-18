package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class SendVerificationUseCase {

    private final SendMessageService sendMessageService;
    private final VerificationProofService verificationProofService;

    public void execute(SendVerificationRequest request) {
        String code = verificationProofService.issue(request.getPhoneNumber(), request.getType());
        String text = String.format(
                "[부산소프트웨어마이스터고] %s 인증번호는 [%s]입니다.",
                request.getType().getDescription(), code);
        try {
            sendMessageService.execute(request.getPhoneNumber(), text);
        } catch (RuntimeException e) {
            verificationProofService.cancel(request.getPhoneNumber(), request.getType(), code);
            throw e;
        }
    }
}
