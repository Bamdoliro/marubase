package com.bamdoliro.maru.domain.auth.exception;

import com.bamdoliro.maru.domain.auth.exception.error.AuthErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class AuthorityMismatchException extends MaruException {

    public AuthorityMismatchException() {
        super(AuthErrorProperty.AUTHORITY_MISMATCH);
    }
}
