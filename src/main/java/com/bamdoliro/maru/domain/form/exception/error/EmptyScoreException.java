package com.bamdoliro.maru.domain.form.exception.error;

import com.bamdoliro.maru.shared.error.MaruException;

public class EmptyScoreException extends MaruException {

    public EmptyScoreException() {
        super(FormErrorProperty.EMPTY_SCORE);
    }
}
