package com.bamdoliro.maru.domain.schedule.exception;

import com.bamdoliro.maru.domain.schedule.exception.error.ScheduleErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FirstPassAlreadyExecutedException extends MaruException {

    public FirstPassAlreadyExecutedException() {
        super(ScheduleErrorProperty.FIRST_PASS_ALREADY_EXECUTED);
    }
}
