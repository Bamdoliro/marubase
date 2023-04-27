package com.bamdoliro.maru.infrastructure.mail;

import com.bamdoliro.maru.infrastructure.mail.exception.FailedToSendMailException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@SpringBootTest
class SendEmailServiceTest {

    @Autowired
    private SendEmailService sendEmailService;

    @Test
    void 메일을_전송한다() throws Exception {
        sendEmailService.execute("gimhanuly@gmail.com", "테스트 메일", "테스트 메일입니다.");
    }

    @Test
    void 없는_주소로_메일을_전송하면_에러가_발생한다() throws Exception {
        assertThrows(FailedToSendMailException.class,
                () -> sendEmailService.execute("누가봐도@없는.이메일", "어차피안감", "진심임"));
    }
}