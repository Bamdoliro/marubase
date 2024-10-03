package com.bamdoliro.maru.presentation.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownloadFileResponse {

    private final String downloadUrl;
    private final String fileName;
}
