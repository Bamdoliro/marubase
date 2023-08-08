package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QuerySubmittedFormUseCase {

    private final FormRepository formRepository;

    @Transactional(readOnly = true)
    public List<FormSimpleResponse> execute() {
        return formRepository.findByStatus(FormStatus.FINAL_SUBMITTED)
                .stream()
                .map(FormSimpleResponse::new)
                .toList();
    }
}
