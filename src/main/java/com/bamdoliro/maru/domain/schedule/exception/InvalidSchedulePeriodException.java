package com.bamdoliro.maru.domain.schedule.exception;

import com.bamdoliro.maru.domain.schedule.exception.error.ScheduleErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class InvalidSchedulePeriodException extends MaruException {

    public InvalidSchedulePeriodException() {
        super(ScheduleErrorProperty.INVALID_SCHEDULE_PERIOD);
    }
}
