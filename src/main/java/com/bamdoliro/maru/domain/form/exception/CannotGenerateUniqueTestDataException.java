package com.bamdoliro.maru.domain.form.exception;

import com.bamdoliro.maru.domain.form.exception.error.FormErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class CannotGenerateUniqueTestDataException extends MaruException {

    public CannotGenerateUniqueTestDataException() {
        super(FormErrorProperty.CANNOT_GENERATE_UNIQUE_TEST_DATA);
    }
}
