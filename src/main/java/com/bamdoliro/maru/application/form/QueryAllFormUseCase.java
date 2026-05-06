package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@UseCase
public class QueryAllFormUseCase {

    private final FormRepository formRepository;

    public List<FormSimpleResponse> execute(FormStatus status, FormType type, String sort) {
        List<Form> formList = new ArrayList<>(formRepository.findByStatus(status).stream()
                .filter(form -> Objects.isNull(type) || form.getType().equals(type))
                .toList());

        if (sort != null) {
            switch (sort) {
                case "total-score-asc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.nullsLast(Comparator.naturalOrder())));
                case "total-score-desc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.nullsLast(Comparator.reverseOrder())));
                case "form-id" -> formList.sort(Comparator.comparing(Form::getId));
                default -> formList.sort(Comparator.comparing(Form::getExaminationNumber));
            }
        } else {
            formList.sort(Comparator.comparing(Form::getExaminationNumber));
        }

        return formList.stream()
                .map(FormSimpleResponse::new)
                .toList();
    }

    // 하위 호환성을 유지해야하므로 execute 함수를 오버로드
    public List<FormSimpleResponse> execute(FormStatus status, FormType type, String sort, int page, int size) {
        List<FormSimpleResponse> allForms = this.execute(status, type, sort);

        int skip = (page - 1) * size;

        return allForms.stream()
                .skip(skip)
                .limit(size)
                .collect(Collectors.toList());
    }
}