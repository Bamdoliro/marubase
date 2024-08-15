package com.bamdoliro.maru.presentation.notice.dto.response;

import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import lombok.Getter;

@Getter
public class UploadFileResponse {

    private final UrlResponse url;

    private final String fileName;

    public UploadFileResponse(UrlResponse url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }
}
