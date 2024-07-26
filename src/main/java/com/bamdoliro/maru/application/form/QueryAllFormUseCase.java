package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@UseCase
public class QueryAllFormUseCase {

    private final FormRepository formRepository;

    public List<FormSimpleResponse> execute(FormStatus status, FormType.Category category, String sort) {
        List<Form> forms = new java.util.ArrayList<>(formRepository.findByStatus(status).stream()
                .filter(form -> Objects.isNull(category) || form.getType().categoryEquals(category))
                .toList());

        if (sort.equals("total-score-asc")) {
            forms.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.naturalOrder()));
        } else if (sort.equals("total-score-desc")) {
            forms.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.reverseOrder()));
        }

        return forms.stream()
                .map(FormSimpleResponse::new)
                .collect(Collectors.toList());
    }
}