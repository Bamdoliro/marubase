package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.form.service.FormService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormDraftRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class SubmitFormUseCase {

    private final FormFacade formFacade;

    @Transactional
    public void execute(User user, SubmitFormRequest request) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        form.submit(request.getFormUrl());
    }

    private void validateFormStatus(Form form) {
        if (!(form.isDraft() || form.isRejected())) {
            throw new FormAlreadySubmittedException();
        }
    }
}
