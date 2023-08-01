package com.bamdoliro.maru.domain.notice.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorProperty implements ErrorProperty {
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
