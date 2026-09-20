package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidGradeException extends MaruException {

    public InvalidGradeException(String missingGradeLabel) {
        super(FormErrorProperty.INVALID_GRADE, missingGradeLabel);
    }
}
