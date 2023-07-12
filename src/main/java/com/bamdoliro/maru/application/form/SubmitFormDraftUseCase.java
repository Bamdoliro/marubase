package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormDraftRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class SubmitFormDraftUseCase {

    private final FormRepository formRepository;
    private final FormService formService;

    @Transactional
    public void execute(User user, SubmitFormDraftRequest request) {
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

        formService.calculateScore(form);

        formRepository.save(form);
    }

    private void validateOnlyOneFormPerUser(User user) {
        if (formRepository.existsByUserId(user.getId())) {
            throw new FormAlreadySubmittedException();
        }
    }
}