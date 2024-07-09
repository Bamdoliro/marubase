package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.analysis.dto.request.GradeDistributionRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.GradeDistributionResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QueryGradeDistributionUseCase {

    private final FormRepository formRepository;

    public List<GradeDistributionResponse> execute(GradeDistributionRequest request) {
        List<GradeDistributionResponse> result = new java.util.ArrayList<>(formRepository.findGradeGroupByTypeAndStatus(request.getStatusList())
                .stream()
                .map(GradeDistributionResponse::new)
                .toList());

        List<FormType> existingTypes = result
                .stream()
                .map(GradeDistributionResponse::getType)
                .toList();

        for (FormType formType : FormType.values()) {
            if (!existingTypes.contains(formType)) {
                result.add(new GradeDistributionResponse(formType, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
            }
        }

        return result;
    }
}