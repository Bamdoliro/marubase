package com.bamdoliro.maru.domain.user.exception;

import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class VerificationAttemptsExceededException extends MaruException {

    public VerificationAttemptsExceededException() {
        super(UserErrorProperty.VERIFICATION_ATTEMPTS_EXCEEDED);
    }
}
