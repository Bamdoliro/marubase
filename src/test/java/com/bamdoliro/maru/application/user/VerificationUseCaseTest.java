package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UpdatePasswordVerificationRepository;
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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VerificationUseCaseTest {

    @InjectMocks
    private SendVerificationUseCase sendVerificationUseCase;

    @Mock
    private SendMessageService sendMessageService;

    @Mock
    private SignUpVerificationRepository signUpVerificationRepository;

    @Mock
    private UpdatePasswordVerificationRepository updatePasswordVerificationRepository;

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

    @Test
    void 유저가_비밀번호_변경_전화번호_인증을_요청한다() {
        // given
        UpdatePasswordVerification verification = UserFixture.createUpdatePasswordVerification(false);
        willDoNothing().given(sendMessageService).execute(anyString(), anyString());
        given(updatePasswordVerificationRepository.save(any(UpdatePasswordVerification.class))).willReturn(verification);

        // when
        sendVerificationUseCase.execute(new SendVerificationRequest(verification.getPhoneNumber(), VerificationType.UPDATE_PASSWORD));

        // then
        verify(sendMessageService, times(1)).execute(anyString(), anyString());
        verify(updatePasswordVerificationRepository, times(1)).save(any(UpdatePasswordVerification.class));

        assertNotNull(verification.getCode());
    }

    @Test
    void 회원가입_비밀번호_변경전화번호_전송이_실패한다() {
        // given
        UpdatePasswordVerification verification = UserFixture.createUpdatePasswordVerification(false);
        doThrow(new FailedToSendException()).when(sendMessageService).execute(anyString(), anyString());

        // when and then
        assertThrows(FailedToSendException.class,
                () -> sendVerificationUseCase.execute(new SendVerificationRequest(verification.getPhoneNumber(), VerificationType.UPDATE_PASSWORD)));

        verify(sendMessageService, times(1)).execute(anyString(), anyString());
        verify(updatePasswordVerificationRepository, never()).save(any(UpdatePasswordVerification.class));
    }
}