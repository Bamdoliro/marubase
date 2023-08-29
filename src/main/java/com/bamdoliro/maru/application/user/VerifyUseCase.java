package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class VerifyUseCase {

    private final VerificationRepository verificationRepository;

    public void execute(VerifyRequest request) {
        Verification verification = getVerification(request.getPhoneNumber());

        if (!verification.getCode().equals(request.getCode())) {
            throw new VerificationCodeMismatchException();
        }

        verificationRepository.updateVerification(
                verification.getPhoneNumber(),
                true
        );
    }

    private Verification getVerification(String phoneNumber) {
        return verificationRepository.findById(phoneNumber)
                .orElseThrow(VerifyingHasFailedException::new);
    }
}
