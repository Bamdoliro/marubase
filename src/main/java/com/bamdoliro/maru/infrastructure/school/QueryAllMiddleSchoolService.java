package com.bamdoliro.maru.infrastructure.school;

import com.bamdoliro.maru.infrastructure.school.feign.SchoolInfoClient;
import com.bamdoliro.maru.infrastructure.school.feign.dto.response.SchoolInfoResponse;
import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.bamdoliro.maru.shared.config.properties.SchoolInfoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QueryAllMiddleSchoolService {

    private final SchoolInfoProperties schoolInfoProperties;
    private final SchoolInfoClient schoolInfoClient;

    public List<SchoolResponse> execute() {
        SchoolInfoResponse response = schoolInfoClient.getSchoolInfo(schoolInfoProperties.getKey(), LocalDate.now().getYear());

        return response.getSchoolList().stream()
                .map(s -> SchoolResponse.builder()
                        .name(s.getName())
                        .location(s.getLocation())
                        .code(s.getCode())
                        .build())
                .toList();
    }
}
