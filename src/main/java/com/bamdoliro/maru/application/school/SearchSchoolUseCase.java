package com.bamdoliro.maru.application.school;

import com.bamdoliro.maru.infrastructure.school.QueryAllMiddleSchoolService;
import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SearchSchoolUseCase {

    private final QueryAllMiddleSchoolService queryAllMiddleSchoolService;

    public List<SchoolResponse> execute() {
        return queryAllMiddleSchoolService.execute();
    }
}
