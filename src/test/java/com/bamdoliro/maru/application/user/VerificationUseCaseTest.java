package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationUseCaseTest {

    private final VerificationProofService proofs = mock(VerificationProofService.class);
    private final SendMessageService messages = mock(SendMessageService.class);
    private final SendVerificationUseCase useCase = new SendVerificationUseCase(messages, proofs);

    @ParameterizedTest
    @EnumSource(VerificationType.class)
    void 인증번호를_저장하고_문자를_발송한다(VerificationType type) {
        when(proofs.issue("01000000000", type)).thenReturn("123456");
        useCase.execute(new SendVerificationRequest("01000000000", type));
        var order = inOrder(proofs, messages);
        order.verify(proofs).issue("01000000000", type);
        order.verify(messages).execute(eq("01000000000"), contains("[123456]"));
        verify(proofs, never()).cancel(any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(VerificationType.class)
    void 발송이_실패하면_해당_인증번호만_취소한다(VerificationType type) {
        when(proofs.issue("01000000000", type)).thenReturn("123456");
        doThrow(new FailedToSendException()).when(messages).execute(any(), any());
        assertThrows(FailedToSendException.class,
                () -> useCase.execute(new SendVerificationRequest("01000000000", type)));
        verify(proofs).cancel("01000000000", type, "123456");
    }
}
