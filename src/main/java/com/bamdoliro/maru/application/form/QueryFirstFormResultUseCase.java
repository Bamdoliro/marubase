package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.response.FormResultResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryFirstFormResultUseCase {

    private final FormFacade formFacade;

    public FormResultResponse execute(User user) {
        Form form = formFacade.getForm(user);
        return new FormResultResponse(form, form.isFirstPassedNow());
    }
}
