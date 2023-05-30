package com.bamdoliro.maru.domain.form.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FormErrorProperty implements ErrorProperty {

    FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "원서를 찾을 수 없습니다."),
    FORM_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "원서는 한 번만 제출할 수 있습니다.");

    private final HttpStatus status;
    private final String message;
}
