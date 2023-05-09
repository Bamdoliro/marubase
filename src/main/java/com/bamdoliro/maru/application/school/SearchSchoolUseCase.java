package com.bamdoliro.maru.application.school;

import com.bamdoliro.maru.infrastructure.neis.SearchSchoolService;
import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SearchSchoolUseCase {

    private final SearchSchoolService searchSchoolService;

    public List<SchoolResponse> execute(String q) throws JsonProcessingException {
        return searchSchoolService.execute(q);
    }
}
