package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidFormStatusException extends MaruException {

    public InvalidFormStatusException() {
        super(FormErrorProperty.INVALID_FORM_STATUS);
    }
}
