package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class ReceiveFormUseCase {

    private final FormFacade formFacade;

    @Transactional
    public void execute(Long formId) {
        Form form = formFacade.getForm(formId);
        form.receive();
    }
}
