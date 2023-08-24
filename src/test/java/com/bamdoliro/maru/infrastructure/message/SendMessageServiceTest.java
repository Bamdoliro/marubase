package com.bamdoliro.maru.infrastructure.message;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class SendMessageServiceTest {

    @Autowired
    private SendMessageService sendMessageService;

    @Test
    void 메시지를_보낸다() {
        sendMessageService.execute("01094564523", "0_0v");
    }
}