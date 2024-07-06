package com.bamdoliro.maru.application.message;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByStatusRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByTypeRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SendMessageUseCase {

    private final FormRepository formRepository;
    private final SendMessageService sendMessageService;

    public void execute(SendMessageByStatusRequest request) {
        List<Form> formList = formRepository.findByStatus(request.getStatus());
        List<String> phoneNumberList = phoneNumberListConvert(formList);

        sendMessageService.execute(phoneNumberList, request.getText(), request.getTitle());
    }

    public void execute(SendMessageByTypeRequest request) {
        List<Form> formList = formListFilter(request.getFormType(), request.getIsChangeToRegular());
        List<String> phoneNumberList = phoneNumberListConvert(formList);

        sendMessageService.execute(phoneNumberList, request.getText(), request.getTitle());
    }

    private List<Form> formListFilter(FormType formType, Boolean isChangeToRegular) {
        List<Form> formList;
        if (formType.isRegular()) {
            formList = formRepository.findNotExistsMeisterTalentFirstRoundForm();
        } else if (formType.isMeister() && isChangeToRegular) {
            formList = formRepository.findChangedToRegularFirstRoundForm();
        } else {
            formList = formRepository.findMeisterTalentFirstRoundForm();
        }

        return formList;
    }

    private List<String> phoneNumberListConvert(List<Form> formList) {
        return formList.stream()
                .map(form -> form.getUser().getPhoneNumber())
                .toList();
    }
}
