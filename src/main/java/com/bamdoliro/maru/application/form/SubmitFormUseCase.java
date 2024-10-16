package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@UseCase
public class SubmitFormUseCase {

    private final FormRepository formRepository;
    private final CalculateFormScoreService calculateFormScoreService;
    private final AssignExaminationNumberService assignExaminationNumberService;

    @Transactional
    public void execute(User user, SubmitFormRequest request) {
        validateOnlyOneFormPerUser(user);

        Form form = Form.builder()
                .applicant(request.getApplicant().toValue())
                .parent(request.getParent().toValue())
                .education(request.getEducation().toValue())
                .grade(request.getGrade().toValue())
                .document(request.getDocument().toValue())
                .type(request.getType())
                .user(user)
                .build();

        calculateFormScoreService.execute(form);
        assignExaminationNumberService.execute(form);
        formRepository.save(form);
    }

    private void validateOnlyOneFormPerUser(User user) {
        Optional<Form> form = formRepository.findByUser(user);
        if (form.isPresent()) {
            if (form.get().isRejected()) {
                formRepository.delete(form.get());
                return;
            }
            throw new FormAlreadySubmittedException();
        }
    }
}
