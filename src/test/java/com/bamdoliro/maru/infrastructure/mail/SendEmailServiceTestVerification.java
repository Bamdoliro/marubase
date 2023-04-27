package com.bamdoliro.maru.infrastructure.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class SendEmailServiceTestVerification {

    @Autowired
    private SendEmailService sendEmailService;

    @Test
    void 메일을_전송한다() throws Exception {
        sendEmailService.execute("gimhanuly@gmail.com", "테스트 메일", "테스트 메일입니다.");
    }
}