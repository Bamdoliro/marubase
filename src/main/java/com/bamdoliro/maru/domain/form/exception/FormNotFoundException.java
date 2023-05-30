package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FormNotFoundException extends MaruException {

    public FormNotFoundException() {
        super(FormErrorProperty.FORM_NOT_FOUND);
    }
}
