package com.bamdoliro.maru.infrastructure.aop.schedule;

import com.bamdoliro.maru.domain.form.exception.OutOfApplicationFormPeriodException;
import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Aspect
@Component
public class ScheduleValidationAspect {

    private final AdmissionScheduleFacade admissionScheduleFacade;
    private final Clock serviceClock;

    @Before("@annotation(com.bamdoliro.maru.shared.annotation.ValidateApplicationFormPeriod)")
    public void validateApplicationFormPeriod() {
        // TODO: 원서접수 기간 검증 임시 우회. 재적용 시 아래 if 블록 제거
        if (true) {
            return;
        }

        AdmissionSchedule schedule = admissionScheduleFacade.getCurrentSchedule();
        LocalDateTime now = LocalDateTime.now(serviceClock);
        if (now.isBefore(schedule.getApplicationFormStart())
                || now.isAfter(schedule.getApplicationFormEnd())) {
            throw new OutOfApplicationFormPeriodException();
        }
    }
}
