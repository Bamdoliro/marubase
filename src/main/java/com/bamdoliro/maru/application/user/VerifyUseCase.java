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
import com.bamdoliro.maru.shared.ratelimit.RateLimitExceededException;
import com.bamdoliro.maru.shared.ratelimit.RateLimitPolicy;
import com.bamdoliro.maru.shared.ratelimit.RateLimitService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class VerifyUseCase {

    private final SignUpVerificationRepository signUpVerificationRepository;
    private final UpdatePasswordVerificationRepository updatePasswordVerificationRepository;
    private final RateLimitService rateLimitService;

    public void execute(VerifyRequest request) {

        if (request.getType() == VerificationType.SIGNUP) {
            SignUpVerification signUpVerification = getSignUpVerification(request.getPhoneNumber());

            if (!signUpVerification.getCode().equals(request.getCode())) {
                handleVerificationFailure(request, () -> signUpVerificationRepository.delete(signUpVerification));
            }

            signUpVerificationRepository.updateSignUpVerification(
                    signUpVerification.getPhoneNumber(),
                    true
            );
            resetVerificationFailure(request);
        } else {
            UpdatePasswordVerification updatePasswordVerification = getUpdatePasswordVerification(request.getPhoneNumber());

            if (!updatePasswordVerification.getCode().equals(request.getCode())) {
                handleVerificationFailure(request, () -> updatePasswordVerificationRepository.delete(updatePasswordVerification));
            }

            updatePasswordVerificationRepository.updatePasswordVerification(
                    updatePasswordVerification.getPhoneNumber(),
                    true
            );
            resetVerificationFailure(request);
        }
    }

    private void handleVerificationFailure(VerifyRequest request, Runnable deleteVerification) {
        long failureCount = rateLimitService.increment(
                RateLimitPolicy.VERIFICATION_FAILURE,
                createVerificationFailureKey(request)
        );
        if (failureCount >= RateLimitPolicy.VERIFICATION_FAILURE.getMaxRequests()) {
            deleteVerification.run();
            resetVerificationFailure(request);
            throw new RateLimitExceededException();
        }
        throw new VerificationCodeMismatchException();
    }

    private void resetVerificationFailure(VerifyRequest request) {
        rateLimitService.reset(
                RateLimitPolicy.VERIFICATION_FAILURE,
                createVerificationFailureKey(request)
        );
    }

    private String createVerificationFailureKey(VerifyRequest request) {
        return request.getType().name() + ":" + request.getPhoneNumber();
    }

    private SignUpVerification getSignUpVerification(String phoneNumber) {
        return signUpVerificationRepository.findById(phoneNumber)
                .orElseThrow(VerifyingHasFailedException::new);
    }

    private UpdatePasswordVerification getUpdatePasswordVerification(String phoneNumber) {
        return updatePasswordVerificationRepository.findById(phoneNumber)
                .orElseThrow(VerifyingHasFailedException::new);
    }
}
