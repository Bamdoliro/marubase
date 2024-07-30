package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class TotalScoreMissingException extends MaruException {
    public TotalScoreMissingException() {
        super(FormErrorProperty.MISSING_TOTAL_SCORE);
    }
}
