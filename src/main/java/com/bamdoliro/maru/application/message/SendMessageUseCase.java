package com.bamdoliro.maru.application.message;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SendMessageUseCase {

    private final FormRepository formRepository;
    private final SendMessageService sendMessageService;

    public void execute(SendMessageRequest request) {
        List<Form> formList = formRepository.findByStatus(request.getStatus());
        List<String> phoneNumberList = formList.stream().map(form -> form.getUser().getPhoneNumber()).toList();
        sendMessageService.execute(phoneNumberList, request.getText(), request.getTitle());
    }
}
