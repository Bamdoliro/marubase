package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FileSizeLimitExceededException extends MaruException {

    public FileSizeLimitExceededException() {
        super(S3ErrorProperty.FILE_SIZE_LIMIT_EXCEEDED);
    }
}
