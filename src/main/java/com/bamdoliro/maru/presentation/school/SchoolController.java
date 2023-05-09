package com.bamdoliro.maru.presentation.school;

import com.bamdoliro.maru.application.school.SearchSchoolUseCase;
import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/school")
@RestController
public class SchoolController {

    private final SearchSchoolUseCase searchSchoolUseCase;

    @GetMapping
    public ListCommonResponse<SchoolResponse> searchSchool(
            @RequestParam String q) throws JsonProcessingException {
        return ListCommonResponse.ok(
                searchSchoolUseCase.execute(q)
        );
    }
}
