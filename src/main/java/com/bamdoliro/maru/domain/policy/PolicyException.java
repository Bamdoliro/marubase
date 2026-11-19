package com.bamdoliro.maru.domain.policy;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class PolicyException extends MaruException {
    public PolicyException(Reason reason) {
        super(reason);
    }

    @Getter
    @RequiredArgsConstructor
    public enum Reason implements ErrorProperty {
        POLICY_VERSION_CONFLICT(HttpStatus.CONFLICT, "정책이 변경되었습니다. 다시 조회해주세요."),
        SELECTION_POLICY_LOCKED(HttpStatus.CONFLICT, "접수 시작, 원서 생성 또는 선발 후에는 선발 정책을 수정할 수 없습니다."),
        INVALID_POLICY(HttpStatus.BAD_REQUEST, "선발 정원 합계는 1 이상 1000 이하여야 합니다.");

        private final HttpStatus status;
        private final String message;
    }
}
