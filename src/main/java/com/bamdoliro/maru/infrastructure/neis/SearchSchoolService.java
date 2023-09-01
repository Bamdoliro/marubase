package com.bamdoliro.maru.infrastructure.neis;

import com.bamdoliro.maru.infrastructure.neis.feign.NeisClient;
import com.bamdoliro.maru.infrastructure.neis.feign.dto.response.NeisSchoolResponse;
import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.bamdoliro.maru.shared.config.properties.NeisProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchSchoolService {

    private final NeisProperties neisProperties;
    private final NeisClient neisClient;
    private final ObjectMapper objectMapper;

    public List<SchoolResponse> execute(String q) throws JsonProcessingException {
        String htmlResponse = neisClient.getSchoolInfo(neisProperties.getKey(), q, "중학교");
        NeisSchoolResponse response = objectMapper.readValue(htmlResponse, NeisSchoolResponse.class);

        return response.getSchoolInfo().stream()
                .map(s -> SchoolResponse.builder()
                        .name(s.getSchoolName())
                        .location(s.getLocation())
                        .code(s.getStandardSchoolCode())
                        .build())
                .toList();
    }
}
