package com.bamdoliro.maru.infrastructure.scheduler;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.application.schedule.ExecuteFirstPassSelectionUseCase;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.shared.config.TimeConfig;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdmissionScheduleTaskSchedulerTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private AdmissionScheduleFacade admissionScheduleFacade;

    @Mock
    private ExecuteFirstPassSelectionUseCase executeFirstPassSelectionUseCase;

    @Test
    void 서버가_시작되면_미래의_1차_선발을_예약한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        AdmissionScheduleTaskScheduler scheduler = new AdmissionScheduleTaskScheduler(
                taskScheduler,
                admissionScheduleFacade,
                executeFirstPassSelectionUseCase,
                fixedClock(schedule.getApplicationFormStart())
        );

        scheduler.scheduleOnStartup();

        verify(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
    }

    @Test
    void 이미_지난_1차_선발은_예약하지_않는다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        AdmissionScheduleTaskScheduler scheduler = new AdmissionScheduleTaskScheduler(
                taskScheduler,
                admissionScheduleFacade,
                executeFirstPassSelectionUseCase,
                fixedClock(schedule.getAnnouncementOfFirstPass())
        );

        scheduler.scheduleOnStartup();

        verify(taskScheduler, never()).schedule(any(Runnable.class), any(Instant.class));
    }

    private Clock fixedClock(java.time.LocalDateTime dateTime) {
        Instant instant = dateTime.atZone(TimeConfig.SERVICE_ZONE_ID).toInstant();
        return Clock.fixed(instant, TimeConfig.SERVICE_ZONE_ID);
    }
}
