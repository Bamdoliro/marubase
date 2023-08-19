package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidFromStatusException extends MaruException {

    public InvalidFromStatusException() {
        super(FormErrorProperty.INVALID_FORM_STATUS);
    }
}
