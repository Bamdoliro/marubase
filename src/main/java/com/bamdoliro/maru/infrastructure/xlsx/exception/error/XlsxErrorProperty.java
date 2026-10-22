package com.bamdoliro.maru.infrastructure.xlsx.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum XlsxErrorProperty implements ErrorProperty {
    XLSX_TEMPLATE_LOAD_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "엑셀 양식을 불러올 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
