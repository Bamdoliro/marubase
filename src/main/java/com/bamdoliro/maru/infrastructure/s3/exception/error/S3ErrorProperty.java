package com.bamdoliro.maru.infrastructure.s3.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorProperty implements ErrorProperty {
    FAILED_TO_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "저장에 실패했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비었습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 이름이 너무 길거나 없습니다."),
    OVER_FILE_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "파일 크기가 너무 큽니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
