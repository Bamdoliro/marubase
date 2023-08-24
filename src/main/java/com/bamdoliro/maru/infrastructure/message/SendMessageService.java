package com.bamdoliro.maru.infrastructure.message;

import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.shared.config.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendMessageService {

    private final MessageProperties messageProperties;
    private final DefaultMessageService messageService;

    public void execute(String to, String text) {
        Message message = new Message();
        message.setFrom(messageProperties.getFrom());
        message.setTo(to);
        message.setText(text);

        try {
            messageService.sendOne(
                    new SingleMessageSendingRequest(message)
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FailedToSendException();
        }
    }
}
