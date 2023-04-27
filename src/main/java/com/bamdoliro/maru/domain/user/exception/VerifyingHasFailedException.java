package com.bamdoliro.maru.domain.user.exception;

import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class VerifyingHasFailedException extends MaruException {

    public VerifyingHasFailedException() {
        super(UserErrorProperty.VERIFYING_HAS_FAILED);
    }
}
