package com.bamdoliro.maru.domain.schedule.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorProperty implements ErrorProperty {
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 입학 일정을 찾을 수 없습니다."),
    SCHEDULE_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 학년도의 입학 일정이 이미 존재합니다."),
    INVALID_SCHEDULE_PERIOD(HttpStatus.BAD_REQUEST, "입학 일정의 시간 순서가 올바르지 않습니다."),
    SCHEDULE_VERSION_CONFLICT(
            HttpStatus.CONFLICT,
            "다른 관리자가 일정을 수정했습니다. 새로고침 후 다시 시도해주세요."
    ),
    FIRST_PASS_ALREADY_EXECUTED(HttpStatus.CONFLICT, "이미 1차 합격자 선발이 실행된 일정입니다.");

    private final HttpStatus status;
    private final String message;
}
