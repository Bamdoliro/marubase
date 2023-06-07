package com.bamdoliro.maru.domain.auth.exception;

import com.bamdoliro.maru.domain.auth.exception.error.AuthErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class EmptyTokenException extends MaruException {

    public EmptyTokenException() {
        super(AuthErrorProperty.EMPTY_TOKEN);
    }
}
