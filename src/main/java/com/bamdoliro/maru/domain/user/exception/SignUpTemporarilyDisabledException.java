package com.bamdoliro.maru.domain.user.exception;

import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class SignUpTemporarilyDisabledException extends MaruException {

    public SignUpTemporarilyDisabledException() {
        super(UserErrorProperty.SIGNUP_TEMPORARILY_DISABLED);
    }
}
