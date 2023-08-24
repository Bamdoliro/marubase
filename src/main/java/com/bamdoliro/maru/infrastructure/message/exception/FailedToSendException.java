package com.bamdoliro.maru.infrastructure.message.exception;

import com.bamdoliro.maru.infrastructure.message.exception.error.MessageErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FailedToSendException extends MaruException {

    public FailedToSendException() {
        super(MessageErrorProperty.FAILED_TO_SEND);
    }
}
