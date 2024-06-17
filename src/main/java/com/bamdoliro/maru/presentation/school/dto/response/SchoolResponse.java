package com.bamdoliro.maru.presentation.school.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SchoolResponse {

    private String name;
    private String location;
    private String address;
    private String code;
}
