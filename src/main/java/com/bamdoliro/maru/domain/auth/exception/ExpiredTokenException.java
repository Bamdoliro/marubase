package com.bamdoliro.maru.domain.auth.exception;


import com.bamdoliro.maru.domain.auth.exception.error.AuthErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class ExpiredTokenException extends MaruException {

    public ExpiredTokenException() {
        super(AuthErrorProperty.EXPIRED_TOKEN);
    }
}
