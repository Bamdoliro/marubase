package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class ExaminationNumberExhaustedException extends MaruException {

    public ExaminationNumberExhaustedException() {
        super(FormErrorProperty.EXAMINATION_NUMBER_EXHAUSTED);
    }
}
