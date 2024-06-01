package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdatePasswordUseCase {

    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    @Transactional
    public void execute(UpdatePasswordRequest request) {
        validate(request);

        User user = userFacade.getUser(request.getPhoneNumber());
        user.updatePassword(request.getPassword());
    }

    private void validate(UpdatePasswordRequest request) {
        Verification verification = verificationRepository.findById(request.getPhoneNumber())
                .orElseThrow(VerifyingHasFailedException::new);

        if (!verification.getIsVerified()) {
            throw new VerifyingHasFailedException();
        }

        if (!userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserNotFoundException();
        }

        verificationRepository.updateVerification(
                request.getPhoneNumber(),
                false
        );
    }
}
