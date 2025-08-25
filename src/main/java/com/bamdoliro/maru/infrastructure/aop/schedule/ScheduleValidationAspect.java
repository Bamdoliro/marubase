package com.bamdoliro.maru.infrastructure.aop.schedule;

import com.bamdoliro.maru.domain.form.exception.OutOfApplicationFormPeriodException;
import com.bamdoliro.maru.shared.constants.Schedule;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Aspect
@Component
public class ScheduleValidationAspect {


    @Before("@annotation(com.bamdoliro.maru.shared.annotation.ValidateApplicationFormPeriod)")
    public void validateApplicationFormPeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(Schedule.START) || now.isAfter(Schedule.END)) {
            throw new OutOfApplicationFormPeriodException();
        }
    }
}
