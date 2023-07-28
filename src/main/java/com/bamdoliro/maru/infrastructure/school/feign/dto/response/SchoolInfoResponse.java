package com.bamdoliro.maru.infrastructure.school.feign.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolInfoResponse {

    private String resultCode;
    private String resultMsg;

    @JsonProperty("list")
    private List<SchoolInfo> schoolList;

    @Getter
    @NoArgsConstructor
    public static class SchoolInfo {

        @JsonProperty("SCHUL_CODE")
        public String code;

        @JsonProperty("SCHUL_NM")
        public String name;

        @JsonProperty("ADRCD_NM")
        public String location;
    }
}
