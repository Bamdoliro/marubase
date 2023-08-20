package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidFileException extends MaruException {

    public InvalidFileException() {
        super(FormErrorProperty.INVALID_FILE);
    }
}
