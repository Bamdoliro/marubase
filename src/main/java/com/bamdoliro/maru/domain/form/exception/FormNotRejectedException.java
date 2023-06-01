package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FormNotRejectedException extends MaruException {

    public FormNotRejectedException() {
        super(FormErrorProperty.FORM_NOT_REJECTED);
    }
}
