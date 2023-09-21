package com.bamdoliro.maru.domain.auth.exception;

import com.bamdoliro.maru.domain.auth.exception.error.AuthErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class WrongLoginException extends MaruException {

    public WrongLoginException() {
        super(AuthErrorProperty.WRONG_LOGIN);
    }
}
