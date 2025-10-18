package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.analysis.dto.request.SchoolStatusRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.SchoolStatusResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QuerySchoolStatusUseCase {

    private final FormRepository formRepository;

    public List<SchoolStatusResponse> execute(SchoolStatusRequest request) {
        Boolean isBusan = request.getIsBusan();

        if(isBusan == null){
            return formRepository.findAllForms(request.getStatusList())
                    .stream()
                    .filter(vo -> vo.getSchoolName() != null && vo.getSchoolAddress() != null)
                    .map(SchoolStatusResponse::new)
                    .sorted(Comparator.comparing(SchoolStatusResponse::getApplicantName))
                    .toList();
        }

        if (isBusan) {
            String keyword = "부산광역시";
            keyword += request.getGu() == null ? "" : (" " + request.getGu());

            return formRepository.findSchoolByAddress(request.getStatusList(), keyword)
                    .stream()
                    .filter(vo -> vo.getSchoolName() != null && vo.getSchoolAddress() != null)
                    .map(SchoolStatusResponse::new)
                    .sorted(Comparator.comparing(SchoolStatusResponse::getApplicantName))
                    .toList();
        }

        return formRepository.findNotBusanSchool(request.getStatusList())
                .stream()
                .filter(vo -> vo.getSchoolName() != null && vo.getSchoolAddress() != null)
                .map(SchoolStatusResponse::new)
                .sorted(Comparator.comparing(SchoolStatusResponse::getApplicantName))
                .toList();
    }
}