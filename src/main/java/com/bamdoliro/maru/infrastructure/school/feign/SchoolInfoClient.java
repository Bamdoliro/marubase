package com.bamdoliro.maru.infrastructure.school.feign;

import com.bamdoliro.maru.infrastructure.school.feign.dto.response.SchoolInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "schoolInfo", url = "http://www.schoolinfo.go.kr/openApi.do")
public interface SchoolInfoClient {

    @GetMapping("?apiType=0&schulKndCode=03")
    SchoolInfoResponse getSchoolInfo(
            @RequestParam("apiKey") String apiKey,
            @RequestParam("pbanYr") int pbanYr
    );
}
