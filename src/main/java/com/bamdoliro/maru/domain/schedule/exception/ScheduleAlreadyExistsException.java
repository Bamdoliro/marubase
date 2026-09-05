package com.bamdoliro.maru.domain.schedule.exception;

import com.bamdoliro.maru.domain.schedule.exception.error.ScheduleErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class ScheduleAlreadyExistsException extends MaruException {

    public ScheduleAlreadyExistsException() {
        super(ScheduleErrorProperty.SCHEDULE_ALREADY_EXISTS);
    }
}
