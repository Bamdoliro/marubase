package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class DraftFormNotFoundException extends MaruException {

    public DraftFormNotFoundException() {
        super(FormErrorProperty.DRAFT_FORM_NOT_FOUND);
    }
}
