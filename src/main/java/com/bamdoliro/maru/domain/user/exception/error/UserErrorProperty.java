package com.bamdoliro.maru.domain.user.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorProperty implements ErrorProperty {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입한 사용자입니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
    VERIFYING_HAS_FAILED(HttpStatus.UNAUTHORIZED, "전화번호 인증이 실패했습니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.UNAUTHORIZED, "전화번호 인증 코드가 틀렸습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
