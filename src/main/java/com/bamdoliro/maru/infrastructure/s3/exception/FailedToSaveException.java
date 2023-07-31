package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FailedToSaveException extends MaruException {

    public FailedToSaveException() {
        super(S3ErrorProperty.FAILED_TO_SAVE);
    }
}
