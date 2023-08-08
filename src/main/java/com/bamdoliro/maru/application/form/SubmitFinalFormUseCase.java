package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFinalFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class SubmitFinalFormUseCase {

    private final FormFacade formFacade;

    @Transactional
    public void execute(User user, SubmitFinalFormRequest request) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        form.submit(request.getFormUrl());
    }

    private void validateFormStatus(Form form) {
        if (!(form.isSubmitted() || form.isRejected())) {
            throw new FormAlreadySubmittedException();
        }
    }
}
