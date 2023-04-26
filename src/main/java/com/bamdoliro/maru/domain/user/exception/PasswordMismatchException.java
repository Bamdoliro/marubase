package com.bamdoliro.maru.domain.user.exception;

import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class PasswordMismatchException extends MaruException {

    public PasswordMismatchException() {
        super(UserErrorProperty.PASSWORD_MISMATCH);
    }
}
