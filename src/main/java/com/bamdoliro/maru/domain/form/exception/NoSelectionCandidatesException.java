package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class NoSelectionCandidatesException extends MaruException {

    public NoSelectionCandidatesException() {
        super(FormErrorProperty.NO_SELECTION_CANDIDATES);
    }
}
