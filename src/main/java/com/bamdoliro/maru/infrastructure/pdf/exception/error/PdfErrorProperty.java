package com.bamdoliro.maru.infrastructure.pdf.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PdfErrorProperty implements ErrorProperty {
    FAILED_TO_EXPORT(HttpStatus.INTERNAL_SERVER_ERROR, "PDF 추출을 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
