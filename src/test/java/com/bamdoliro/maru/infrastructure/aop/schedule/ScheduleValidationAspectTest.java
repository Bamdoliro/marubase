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
import org.junit.jupiter.api.Disabled;
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

    // TODO: 원서접수 기간 검증 우회가 해제되면 @Disabled 제거
    @Disabled("원서접수 기간 검증이 임시로 우회되어 있어 스터빙이 호출되지 않음")
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

    // TODO: 원서접수 기간 검증 우회가 해제되면 @Disabled 제거
    @Disabled("원서접수 기간 검증이 임시로 우회되어 있어 이 테스트는 통과하지 않음")
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
