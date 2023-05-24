package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class AlreadySubmittedException extends MaruException {

    public AlreadySubmittedException() {
        super(FormErrorProperty.ALREADY_SUBMITTED);
    }
}
