package com.bamdoliro.maru.domain.auth.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorProperty implements ErrorProperty {

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    AUTHORITY_MISMATCH(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    WRONG_LOGIN(HttpStatus.UNAUTHORIZED, "사용자가 없거나 비밀번호가 틀렸습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
