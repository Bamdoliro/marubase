package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.analysis.dto.response.NumberOfApplicantsResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@UseCase
public class QueryNumberOfApplicantsUseCase {

    private final FormRepository formRepository;

    public List<NumberOfApplicantsResponse> execute(String type) {
        List<NumberOfApplicantsResponse> result = new ArrayList<>();
        if (type.equals("ORIGINAL")) {
            result = formRepository.findOriginalTypeAndCountGroupByType()
                    .stream()
                    .map(NumberOfApplicantsResponse::new)
                    .collect(Collectors.toList());
        } else {
            result = formRepository.findTypeAndCountGroupByType()
                    .stream()
                    .map(NumberOfApplicantsResponse::new)
                    .collect(Collectors.toList());
        }

        List<FormType> existingTypes = result
                .stream()
                .map(NumberOfApplicantsResponse::getType)
                .toList();

        for (FormType formType: FormType.values()) {
            if (!existingTypes.contains(formType)) {
                result.add(new NumberOfApplicantsResponse(formType, 0L));
            }
        }

        return result;
    }
}