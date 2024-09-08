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
        List<Form> formList = new java.util.ArrayList<>(formRepository.findByStatus(status).stream()
                .filter(form -> Objects.isNull(category) || form.getType().categoryEquals(category))
                .toList());

        if (sort != null) {
            switch (sort) {
                case "total-score-asc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.nullsLast(Comparator.naturalOrder())));
                case "total-score-desc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.nullsLast(Comparator.reverseOrder())));
                case "form-id" -> formList.sort(Comparator.comparing(Form::getId));
            }
        }

        return formList.stream()
                .map(FormSimpleResponse::new)
                .collect(Collectors.toList());
    }
}