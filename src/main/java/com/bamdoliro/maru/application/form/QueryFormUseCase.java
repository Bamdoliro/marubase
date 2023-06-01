package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.presentation.form.dto.response.FormResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryFormUseCase {

    private final UserFacade userFacade;
    private final FormFacade formFacade;

    public FormResponse execute(Long id) {
        User user = userFacade.getCurrentUser();
        Form form = formFacade.getForm(id);
        form.validatePermission(user);
        return new FormResponse(form);
    }
}
