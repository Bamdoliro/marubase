package com.bamdoliro.maru.infrastructure.s3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UrlResponse {

    private String uploadUrl;
    private String downloadUrl;
}
