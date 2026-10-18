package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdatePasswordUseCase {

    private final VerificationProofService verificationProofService;
    private final UserFacade userFacade;
    private final AuthSessionService authSessionService;

    @Transactional
    public void execute(UpdatePasswordRequest request) {
        verificationProofService.consume(request.getPhoneNumber(),
                VerificationType.UPDATE_PASSWORD, request.getVerificationToken());

        User user = userFacade.getUserForUpdate(request.getPhoneNumber());
        authSessionService.revoke(user.getUuid());
        user.updatePassword(request.getPassword());
    }

}
