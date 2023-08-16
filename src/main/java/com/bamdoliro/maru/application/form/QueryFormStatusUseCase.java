package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryFormStatusUseCase {

    private final FormFacade formFacade;

    public FormSimpleResponse execute(User user) {
        return new FormSimpleResponse(
                formFacade.getForm(user)
        );
    }
}
