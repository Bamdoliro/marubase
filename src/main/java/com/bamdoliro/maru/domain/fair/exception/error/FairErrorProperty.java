package com.bamdoliro.maru.domain.fair.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FairErrorProperty implements ErrorProperty {
    FAIR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 설명회를 찾을 수 없습니다."),
    HEADCOUNT_EXCEEDED(HttpStatus.CONFLICT, "설명회 신청 인원이 초과되었습니다."),
    NOT_APPLICATION_PERIOD(HttpStatus.CONFLICT, "신청 기간이 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
