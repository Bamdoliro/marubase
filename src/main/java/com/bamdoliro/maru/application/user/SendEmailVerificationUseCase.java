package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@UseCase
public class SendEmailVerificationUseCase {

    private final EmailVerificationRepository emailVerificationRepository;
    private final SendEmailService sendEmailService;
    private final TemplateEngine templateEngine;

    public void execute(String email) throws MessagingException, UnsupportedEncodingException {
        EmailVerification verification = emailVerificationRepository.save(
                new EmailVerification(email));

        sendEmailService.execute(
                email,
                "[부산소프트웨어마이스터고] 이메일 인증을 요청했습니다.",
                setContext(verification.getCode())
        );
    }

    private String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("email-verification", context);
    }
}
