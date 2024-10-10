package com.bamdoliro.maru.domain.auth.exception;

import com.bamdoliro.maru.domain.auth.exception.error.AuthErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidSignatureAlgorithmException extends MaruException {
    public InvalidSignatureAlgorithmException() {
        super(AuthErrorProperty.INVALID_SIGNATURE_ALGORITHM);
    }
}
