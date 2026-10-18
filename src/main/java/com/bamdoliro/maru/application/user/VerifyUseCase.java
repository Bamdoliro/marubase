package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.presentation.user.dto.response.VerificationResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class VerifyUseCase {

    private final VerificationProofService verificationProofService;

    public VerificationResponse execute(VerifyRequest request) {
        String proof = verificationProofService.verify(
                request.getPhoneNumber(), request.getType(), request.getCode());
        return new VerificationResponse(proof, VerificationProofService.EXPIRES_IN_SECONDS);
    }
}
