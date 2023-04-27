package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendEmailVerificationUseCaseTest {

    @InjectMocks
    private SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private ITemplateEngine templateEngine;

    @Test
    void 유저가_이메일_인증을_요청한다() throws MessagingException, UnsupportedEncodingException {
        // given
        EmailVerification verification = UserFixture.createVerification();
        given(emailVerificationRepository.save(any(EmailVerification.class))).willReturn(verification);
        given(templateEngine.process(anyString(), any(Context.class))).willReturn("context");
        willDoNothing().given(sendEmailService).execute(anyString(), anyString(), anyString());

        // when
        sendEmailVerificationUseCase.execute(verification.getEmail());

        // then
        verify(emailVerificationRepository, times(1)).save(any(EmailVerification.class));
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(sendEmailService, times(1)).execute(anyString(), anyString(), anyString());

        assertNotNull(verification.getCode());
    }
}