package com.bamdoliro.maru.domain.auth.exception;

import com.bamdoliro.maru.domain.auth.exception.error.AuthErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidTokenException extends MaruException {

    public InvalidTokenException() {
        super(AuthErrorProperty.INVALID_TOKEN);
    }
}
