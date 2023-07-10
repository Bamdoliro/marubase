package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidFileNameException extends MaruException {

    public InvalidFileNameException() {
        super(S3ErrorProperty.INVALID_FILE_NAME);
    }
}
