package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdateFormUseCase {

    private final UserFacade userFacade;
    private final FormFacade formFacade;

    @Transactional
    public void execute(Long id, FormRequest request) {
        User user = userFacade.getCurrentUser();
        Form form = formFacade.getForm(id);
        form.validatePermission(user);
        form.isRejected();

        form.update(
                request.getApplicant().toValue(),
                request.getParent().toValue(),
                request.getEducation().toValue(),
                request.getGrade().toValue(),
                request.getDocument().toValue(),
                request.getType()
        );
    }
}
