package com.bamdoliro.maru.infrastructure.mail.exception;

import com.bamdoliro.maru.infrastructure.mail.exception.error.MailErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FailedToSendMailException extends MaruException {

    public FailedToSendMailException() {
        super(MailErrorProperty.FAILED_TO_SEND);
    }
}