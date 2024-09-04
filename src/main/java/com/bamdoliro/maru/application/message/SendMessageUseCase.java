package com.bamdoliro.maru.application.message;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByStatusRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByTypeRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageToAllUserRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SendMessageUseCase {

    private final FormRepository formRepository;
    private final SendMessageService sendMessageService;
    private final UserRepository userRepository;

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

    public void execute(SendMessageToAllUserRequest request) {
        List<String> phoneNumberList = userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthority() == Authority.USER)
                .map(User::getPhoneNumber)
                .toList();
        sendMessageService.execute(phoneNumberList, request.getText(), request.getTitle());
    }

    private List<Form> formListFilter(FormType formType, Boolean isChangedToRegular) {
        List<Form> formList;
        if (formType.isMeister()) {
            formList = formRepository.findMeisterTalentFirstRoundForm();
        } else {
            if (isChangedToRegular) formList = formRepository.findChangedToRegularFirstRoundForm();
            else formList = formRepository.findNotExistsMeisterTalentAndChangedToRegularFirstRoundForm();
        }

        return formList;
    }

    private List<String> phoneNumberListConvert(List<Form> formList) {
        return formList
                .stream()
                .map(form -> form.getUser().getPhoneNumber())
                .toList();
    }
}
