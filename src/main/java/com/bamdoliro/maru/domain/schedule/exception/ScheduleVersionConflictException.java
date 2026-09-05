package com.bamdoliro.maru.domain.schedule.exception;

import com.bamdoliro.maru.domain.schedule.exception.error.ScheduleErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class ScheduleVersionConflictException extends MaruException {

    public ScheduleVersionConflictException() {
        super(ScheduleErrorProperty.SCHEDULE_VERSION_CONFLICT);
    }
}
