package com.bamdoliro.maru.infrastructure.s3.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorProperty implements ErrorProperty {
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비었습니다."),
    FILE_SIZE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 %dMB의 파일까지만 업로드할 수 있습니다."),
    MEDIA_TYPE_MISMATCH(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "파일 형식이 다릅니다."),
    INVALID_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "파일 형식이 유효하지 않습니다."),
    FILE_COUNT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 %d개의 파일까지만 업로드할 수 있습니다."),
    FILE_STORAGE_ACCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장소 요청에 실패했습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
