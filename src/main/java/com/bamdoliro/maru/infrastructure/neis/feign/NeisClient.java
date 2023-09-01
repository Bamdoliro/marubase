package com.bamdoliro.maru.infrastructure.neis.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "neis", url = "https://open.neis.go.kr/hub")
public interface NeisClient {

    @GetMapping(value = "/schoolInfo?Type=json&pSize=10")
    String getSchoolInfo(
            @RequestParam("Key") String key,
            @RequestParam("SCHUL_NM") String schoolName,
            @RequestParam(name = "SCHUL_KND_SC_NM", required = false) String schoolType
    );
}
