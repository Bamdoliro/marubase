package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class TooLongNameException extends MaruException {

    public TooLongNameException() {
        super(S3ErrorProperty.TOO_LONG_NAME);
    }
}
