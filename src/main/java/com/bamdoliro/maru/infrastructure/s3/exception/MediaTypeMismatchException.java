package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class MediaTypeMismatchException extends MaruException {

    public MediaTypeMismatchException() {
        super(S3ErrorProperty.MEDIA_TYPE_MISMATCH);
    }
}
