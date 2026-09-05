package com.bamdoliro.maru.infrastructure.aop.schedule;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.form.exception.OutOfApplicationFormPeriodException;
import com.bamdoliro.maru.shared.config.TimeConfig;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ScheduleValidationAspectTest {

    @Mock
    private AdmissionScheduleFacade admissionScheduleFacade;

    @Test
    void 날짜_및_시간_검증에_성공한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        ScheduleValidationAspect scheduleValidationAspect = new ScheduleValidationAspect(
                admissionScheduleFacade,
                fixedClock(schedule.getApplicationFormStart().plusSeconds(1))
        );

        scheduleValidationAspect.validateApplicationFormPeriod();
    }

    @Test
    void 날짜_및_시간_검증에_실패하면_에러가_발생한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        ScheduleValidationAspect scheduleValidationAspect = new ScheduleValidationAspect(
                admissionScheduleFacade,
                fixedClock(schedule.getApplicationFormStart().minusSeconds(1))
        );

        assertThrows(
                OutOfApplicationFormPeriodException.class,
                scheduleValidationAspect::validateApplicationFormPeriod
        );
    }

    private Clock fixedClock(LocalDateTime dateTime) {
        ZoneId zoneId = TimeConfig.SERVICE_ZONE_ID;
        Instant instant = dateTime.atZone(zoneId).toInstant();
        return Clock.fixed(instant, zoneId);
    }
}
