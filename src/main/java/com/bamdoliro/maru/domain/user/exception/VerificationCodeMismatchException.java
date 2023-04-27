package com.bamdoliro.maru.domain.user.exception;

import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class VerificationCodeMismatchException extends MaruException {

    public VerificationCodeMismatchException() {
        super(UserErrorProperty.VERIFICATION_CODE_MISMATCH);
    }
}
