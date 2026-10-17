package com.bamdoliro.maru.domain.fair.exception;

import com.bamdoliro.maru.domain.fair.exception.error.FairErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class AttendeeAlreadyExistsException extends MaruException {

    public AttendeeAlreadyExistsException() {
        super(FairErrorProperty.ATTENDEE_ALREADY_EXISTS);
    }
}
