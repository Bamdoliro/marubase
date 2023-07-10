package com.bamdoliro.maru.infrastructure.s3.exception;

import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class ImageSizeMismatchException extends MaruException {

    public ImageSizeMismatchException() {
        super(S3ErrorProperty.IMAGE_SIZE_MISMATCH);
    }
}
