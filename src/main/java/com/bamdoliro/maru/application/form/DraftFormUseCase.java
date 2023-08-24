package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.DraftFormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class DraftFormUseCase {

    private final DraftFormRepository draftFormRepository;

    public void execute(User user, SubmitFormRequest request) {
        draftFormRepository.save(
                DraftForm.builder()
                        .phoneNumber(user.getPhoneNumber())
                        .submitFormRequest(request)
                        .build()
        );
    }
}
