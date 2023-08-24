package com.bamdoliro.maru.domain.fair.exception;

import com.bamdoliro.maru.domain.fair.exception.error.FairErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class NotApplicationPeriodException extends MaruException {

    public NotApplicationPeriodException() {
        super(FairErrorProperty.NOT_APPLICATION_PERIOD);
    }
}
