package com.bamdoliro.maru.domain.scheduler.exception;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FirstPassScheduleProperty implements ErrorProperty {
    PAST_SCHEDULE(HttpStatus.BAD_REQUEST, "이미 지난 시간으론 일정을 등록할 순 없습니다.");

    private final HttpStatus status;
    private final String message;
}
