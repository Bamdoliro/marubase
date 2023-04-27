package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@UseCase
public class SendEmailVerificationUseCase {

    private final ITemplateEngine templateEngine;
    private final SendEmailService sendEmailService;
    private final EmailVerificationRepository emailVerificationRepository;

    public void execute(String email) {
        EmailVerification verification = new EmailVerification(email);

        sendEmailService.execute(
                email,
                "[부산소프트웨어마이스터고] 이메일 인증을 요청했습니다.",
                setContext(verification.getCode())
        );

        emailVerificationRepository.save(verification);
    }

    private String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("email-verification", context);
    }
}
