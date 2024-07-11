package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.analysis.dto.request.SchoolStatusRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.SchoolStatusResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QuerySchoolStatusUseCase {

    private final FormRepository formRepository;

    public List<SchoolStatusResponse> execute(SchoolStatusRequest request) {
        String keyword = request.getIsBusan() ? "부산광역시" : "";
        keyword += request.getGu() == null ? "" : (" " + request.getGu());

        return formRepository.findSchoolByAddress(request.getStatusList(), keyword)
                .stream()
                .map(SchoolStatusResponse::new)
                .toList();
    }
}