package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.mail.exception.FailedToSendMailException;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

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
class SendEmailVerificationUseCaseTest {

    @InjectMocks
    private SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @Mock
    private ProcessTemplateService processTemplateService;

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;


    @Test
    void 유저가_이메일_인증을_요청한다() {
        // given
        EmailVerification verification = UserFixture.createVerification();
        given(processTemplateService.execute(anyString(), any(Map.class))).willReturn("context");
        willDoNothing().given(sendEmailService).execute(anyString(), anyString(), anyString());
        given(emailVerificationRepository.save(any(EmailVerification.class))).willReturn(verification);

        // when
        sendEmailVerificationUseCase.execute(verification.getEmail());

        // then
        verify(processTemplateService, times(1)).execute(anyString(), any(Map.class));
        verify(sendEmailService, times(1)).execute(anyString(), anyString(), anyString());
        verify(emailVerificationRepository, times(1)).save(any(EmailVerification.class));

        assertNotNull(verification.getCode());
    }

    @Test
    void 이메일_전송이_실패한다() {
        // given
        EmailVerification verification = UserFixture.createVerification();
        given(processTemplateService.execute(anyString(), any(Map.class))).willReturn("context");
        doThrow(new FailedToSendMailException()).when(sendEmailService).execute(anyString(), anyString(), anyString());

        // when and then
        assertThrows(FailedToSendMailException.class,
                () -> sendEmailVerificationUseCase.execute(verification.getEmail()));

        verify(processTemplateService, times(1)).execute(anyString(), any(Map.class));
        verify(sendEmailService, times(1)).execute(anyString(), anyString(), anyString());
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }
}