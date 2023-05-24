package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FormAlreadySubmittedException extends MaruException {

    public FormAlreadySubmittedException() {
        super(FormErrorProperty.FORM_ALREADY_SUBMITTED);
    }
}
