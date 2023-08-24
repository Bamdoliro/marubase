package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.VerificationRequest;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SendVerificationUseCaseTest {

    @InjectMocks
    private SendVerificationUseCase sendVerificationUseCase;

    @Mock
    private SendMessageService sendMessageService;

    @Mock
    private VerificationRepository verificationRepository;


    @Test
    void 유저가_전화번호_인증을_요청한다() {
        // given
        Verification verification = UserFixture.createVerification();
        willDoNothing().given(sendMessageService).execute(anyString(), anyString());
        given(verificationRepository.save(any(Verification.class))).willReturn(verification);

        // when
        sendVerificationUseCase.execute(new VerificationRequest(verification.getPhoneNumber()));

        // then
        verify(sendMessageService, times(1)).execute(anyString(), anyString());
        verify(verificationRepository, times(1)).save(any(Verification.class));

        assertNotNull(verification.getCode());
    }

    @Test
    void 전화번호_전송이_실패한다() {
        // given
        Verification verification = UserFixture.createVerification();
        doThrow(new FailedToSendException()).when(sendMessageService).execute(anyString(), anyString());

        // when and then
        assertThrows(FailedToSendException.class,
                () -> sendVerificationUseCase.execute(new VerificationRequest(verification.getPhoneNumber())));

        verify(sendMessageService, times(1)).execute(anyString(), anyString());
        verify(verificationRepository, never()).save(any(Verification.class));
    }
}