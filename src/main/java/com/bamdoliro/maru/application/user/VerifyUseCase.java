package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UpdatePasswordVerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class VerifyUseCase {

    private final SignUpVerificationRepository signUpVerificationRepository;
    private final UpdatePasswordVerificationRepository updatePasswordVerificationRepository;

    public void execute(VerifyRequest request) {

        if (request.getType() == VerificationType.SIGNUP) {
            SignUpVerification signUpVerification = getSingUpVerification(request.getPhoneNumber());

            if (!signUpVerification.getCode().equals(request.getCode())) {
                throw new VerificationCodeMismatchException();
            }

            signUpVerificationRepository.updateSignUpVerification(
                    signUpVerification.getPhoneNumber(),
                    true
            );
        } else {
            UpdatePasswordVerification updatePasswordVerification = getUpdatePasswordVerification(request.getPhoneNumber());

            if (!updatePasswordVerification.getCode().equals(request.getCode())) {
                throw new VerificationCodeMismatchException();
            }

            updatePasswordVerificationRepository.updatePasswordVerification(
                    updatePasswordVerification.getPhoneNumber(),
                    true
            );
        }
    }

    private SignUpVerification getSingUpVerification(String phoneNumber) {
        return signUpVerificationRepository.findById(phoneNumber)
                .orElseThrow(VerifyingHasFailedException::new);
    }

    private UpdatePasswordVerification getUpdatePasswordVerification(String phoneNumber) {
        return updatePasswordVerificationRepository.findById(phoneNumber)
                .orElseThrow(VerifyingHasFailedException::new);
    }
}
