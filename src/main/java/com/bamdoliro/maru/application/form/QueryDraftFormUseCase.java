package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import com.bamdoliro.maru.domain.form.exception.DraftFormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.DraftFormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryDraftFormUseCase {

    private final DraftFormRepository draftFormRepository;

    public SubmitFormRequest execute(User user) {
        DraftForm draftForm = getDraftForm(user);
        return draftForm.getSubmitFormRequest();
    }

    private DraftForm getDraftForm(User user) {
        return draftFormRepository.findById(user.getPhoneNumber())
                .orElseThrow(DraftFormNotFoundException::new);
    }
}
