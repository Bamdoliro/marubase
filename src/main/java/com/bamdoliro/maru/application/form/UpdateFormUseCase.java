package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.CannotUpdateNotRejectedFormException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdateFormUseCase {

    private final FormFacade formFacade;

    @Transactional
    public void execute(User user, Long id, UpdateFormRequest request) {
        Form form = formFacade.getForm(id);
        form.isApplicant(user);
        validateFormStatus(form);

        form.update(
                request.getApplicant().toValue(),
                request.getParent().toValue(),
                request.getEducation().toValue(),
                request.getGrade().toValue(),
                request.getDocument().toValue(),
                request.getType()
        );
    }

    private void validateFormStatus(Form form) {
        if (!form.isRejected()) {
            throw new CannotUpdateNotRejectedFormException();
        }
    }
}
