package com.bamdoliro.maru.infrastructure.message;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageRequest;
import com.bamdoliro.maru.shared.config.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendMessageService {

    private final MessageProperties messageProperties;
    private final DefaultMessageService messageService;
    private final FormRepository formRepository;

    public void execute(String to, String text) {
        Message message = createMessage(to, text);
        sendOneMessage(message);
    }

    public void execute(String to, String text, String title) {
        Message message = createMessage(to, text);
        message.setSubject(title);
        sendOneMessage(message);
    }

    public void execute(SendMessageRequest request) {
        List<Form> formList = formRepository.findByStatus(request.getStatus());
        List<String> phoneNumberList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();

        for (Form form : formList) {
            phoneNumberList.add(form.getUser().getPhoneNumber());
        }

        for (String phoneNumber : phoneNumberList) {
            Message message = createMessage(phoneNumber, request.getText());
            message.setSubject(request.getTitle());
            messageList.add(message);
        }

        sendManyMessage(messageList);
    }

    private Message createMessage(String to, String text) {
        Message message = new Message();
        message.setFrom(messageProperties.getFrom());
        message.setTo(to);
        message.setText(text);
        return message;
    }

    private void sendOneMessage(Message message) {
        try {
            messageService.sendOne(
                    new SingleMessageSendingRequest(message)
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FailedToSendException();
        }
    }

    private void sendManyMessage(List<Message> messageList) {
        try {
            messageService.send(messageList, false, false);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FailedToSendException();
        }
    }
}
