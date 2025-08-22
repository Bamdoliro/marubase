package com.bamdoliro.maru.domain.scheduler.exception;

import com.bamdoliro.maru.shared.error.MaruException;

public class CannotSchedulePastTimeException extends MaruException {

    public CannotSchedulePastTimeException() {
        super(FirstPassScheduleProperty.PAST_SCHEDULE);
    }
}
