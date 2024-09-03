package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class MissingTotalScoreException extends MaruException {
    public MissingTotalScoreException() {
        super(FormErrorProperty.MISSING_TOTAL_SCORE);
    }
}
