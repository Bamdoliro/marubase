package com.bamdoliro.maru.infrastructure.mail.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorProperty implements ErrorProperty {
    FAILED_TO_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
