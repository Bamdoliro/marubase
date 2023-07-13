package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@UseCase
public class SendEmailVerificationUseCase {

    private final ProcessTemplateService processTemplateService;
    private final SendEmailService sendEmailService;
    private final EmailVerificationRepository emailVerificationRepository;

    public void execute(String email) {
        EmailVerification verification = new EmailVerification(email);
        String template = processTemplateService.execute(Templates.EMAIL_VERIFICATION, Map.of("code", verification.getCode()));

        sendEmailService.execute(
                email,
                "[부산소프트웨어마이스터고] 이메일 인증을 요청했습니다.",
                template
        );

        emailVerificationRepository.save(verification);
    }
}
