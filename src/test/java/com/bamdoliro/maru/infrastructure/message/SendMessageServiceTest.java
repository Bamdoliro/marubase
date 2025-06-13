package com.bamdoliro.maru.infrastructure.message;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("테스트 실행 시 실제 메시지 발송으로 인해 비활성화")
@ActiveProfiles("test")
@SpringBootTest
class SendMessageServiceTest {

    @Autowired
    private SendMessageService sendMessageService;

    @Test
    void 메시지를_보낸다() {
        sendMessageService.execute("01037091289", "0_0v");
    }
}