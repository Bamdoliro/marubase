package com.bamdoliro.maru.domain.schedule.exception;

import com.bamdoliro.maru.domain.schedule.exception.error.ScheduleErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class ScheduleNotFoundException extends MaruException {

    public ScheduleNotFoundException() {
        super(ScheduleErrorProperty.SCHEDULE_NOT_FOUND);
    }
}
