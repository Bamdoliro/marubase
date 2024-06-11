package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
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
class SendSignUpVerificationUseCaseTest {

    @InjectMocks
    private SendVerificationUseCase sendVerificationUseCase;

    @Mock
    private SendMessageService sendMessageService;

    @Mock
    private SignUpVerificationRepository signUpVerificationRepository;


    @Test
    void 유저가_회원가입_전화번호_인증을_요청한다() {
        // given
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(false);
        willDoNothing().given(sendMessageService).execute(anyString(), anyString());
        given(signUpVerificationRepository.save(any(SignUpVerification.class))).willReturn(signUpVerification);

        // when
        sendVerificationUseCase.execute(new SendVerificationRequest(signUpVerification.getPhoneNumber(), VerificationType.SIGNUP));

        // then
        verify(sendMessageService, times(1)).execute(anyString(), anyString());
        verify(signUpVerificationRepository, times(1)).save(any(SignUpVerification.class));

        assertNotNull(signUpVerification.getCode());
    }

    @Test
    void 회원가입_전화번호_전송이_실패한다() {
        // given
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(false);
        doThrow(new FailedToSendException()).when(sendMessageService).execute(anyString(), anyString());

        // when and then
        assertThrows(FailedToSendException.class,
                () -> sendVerificationUseCase.execute(new SendVerificationRequest(signUpVerification.getPhoneNumber(), VerificationType.SIGNUP)));

        verify(sendMessageService, times(1)).execute(anyString(), anyString());
        verify(signUpVerificationRepository, never()).save(any(SignUpVerification.class));
    }
}