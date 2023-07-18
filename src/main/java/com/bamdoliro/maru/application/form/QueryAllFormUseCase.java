package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@UseCase
public class QueryAllFormUseCase {

    private final FormRepository formRepository;

    public List<FormSimpleResponse> execute(FormStatus status, FormType.Category category) {
        return formRepository.findByStatus(status).stream()
                .filter(form -> Objects.nonNull(category) && form.getType().categoryEquals(category))
                .map(FormSimpleResponse::new)
                .collect(Collectors.toList());
    }
}
