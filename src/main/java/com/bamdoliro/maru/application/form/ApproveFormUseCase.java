package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class ApproveFormUseCase {

    private final FormFacade formFacade;

    public void execute(Long id) {
        Form form = formFacade.getForm(id);
        form.approve();
    }
}
