package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidFormResultRequestException extends MaruException {

    public InvalidFormResultRequestException() {
        super(FormErrorProperty.INVALID_FORM_RESULT_REQUEST);
    }
}
