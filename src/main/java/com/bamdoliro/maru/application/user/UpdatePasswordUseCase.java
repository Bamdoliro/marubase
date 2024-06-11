package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.user.UpdatePasswordVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdatePasswordUseCase {

    private final UpdatePasswordVerificationRepository updatePasswordVerificationRepository;
    private final UserFacade userFacade;

    @Transactional
    public void execute(UpdatePasswordRequest request) {
        validate(request);

        User user = userFacade.getUser(request.getPhoneNumber());
        user.updatePassword(request.getPassword());
    }

    private void validate(UpdatePasswordRequest request) {
        UpdatePasswordVerification updatePasswordVerification = updatePasswordVerificationRepository.findById(request.getPhoneNumber())
                .orElseThrow(VerifyingHasFailedException::new);

        if (!updatePasswordVerification.getIsVerified()) {
            throw new VerifyingHasFailedException();
        }

        updatePasswordVerificationRepository.delete(updatePasswordVerification);
    }
}
