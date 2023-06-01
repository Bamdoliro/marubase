package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class CannotUpdateNotRejectedFormException extends MaruException {

    public CannotUpdateNotRejectedFormException() {
        super(FormErrorProperty.CANNOT_UPDATE_NOT_REJECTED_FORM);
    }
}
