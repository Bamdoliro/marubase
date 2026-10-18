package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerificationAttemptsExceededException;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerifyUseCaseTest {

    private final VerificationProofService proofs = mock(VerificationProofService.class);
    private final VerifyUseCase useCase = new VerifyUseCase(proofs);

    @ParameterizedTest
    @EnumSource(VerificationType.class)
    void 인증_성공시_증명과_유효기간을_반환한다(VerificationType type) {
        when(proofs.verify("01000000000", type, "123456")).thenReturn("proof");
        var response = useCase.execute(new VerifyRequest("01000000000", "123456", type));
        assertEquals("proof", response.getVerificationToken());
        assertEquals(300, response.getExpiresIn());
    }

    @ParameterizedTest
    @EnumSource(VerificationType.class)
    void 다섯번_오류로_폐기된_응답을_유지한다(VerificationType type) {
        when(proofs.verify("01000000000", type, "123456")).thenThrow(new VerificationAttemptsExceededException());
        assertThrows(VerificationAttemptsExceededException.class,
                () -> useCase.execute(new VerifyRequest("01000000000", "123456", type)));
    }
}
