package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class EmptyFileException extends MaruException {

    public EmptyFileException() {
        super(S3ErrorProperty.EMPTY_FILE);
    }
}
