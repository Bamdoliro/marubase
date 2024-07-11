package com.bamdoliro.maru.domain.form.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FormErrorProperty implements ErrorProperty {

    FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "원서를 찾을 수 없습니다."),
    DRAFT_FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "임시 저장된 원서를 찾을 수 없습니다."),
    FORM_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "원서는 한 번만 제출할 수 있습니다."),
    CANNOT_UPDATE_NOT_REJECTED_FORM(HttpStatus.CONFLICT, "반려된 원서만 수정할 수 있습니다."),
    INVALID_FORM_STATUS(HttpStatus.CONFLICT, "원서 상태가 유효하지 않습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "잘못된 파일입니다."),
    MISSING_TOTAL_SCORE(HttpStatus.PRECONDITION_FAILED, "최종 점수가 입력되지 않은 원서가 존재합니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
