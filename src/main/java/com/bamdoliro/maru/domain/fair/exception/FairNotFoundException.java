package com.bamdoliro.maru.domain.fair.exception;

import com.bamdoliro.maru.domain.fair.exception.error.FairErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FairNotFoundException extends MaruException {

    public FairNotFoundException() {
        super(FairErrorProperty.FAIR_NOT_FOUND);
    }
}
