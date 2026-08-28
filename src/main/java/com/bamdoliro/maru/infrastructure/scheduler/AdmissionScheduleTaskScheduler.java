package com.bamdoliro.maru.infrastructure.scheduler;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.application.schedule.ExecuteFirstPassSelectionUseCase;
import com.bamdoliro.maru.application.schedule.event.AdmissionScheduleChangedEvent;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.shared.config.TimeConfig;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdmissionScheduleTaskScheduler {

    private final TaskScheduler admissionTaskScheduler;
    private final AdmissionScheduleFacade admissionScheduleFacade;
    private final ExecuteFirstPassSelectionUseCase executeFirstPassSelectionUseCase;
    private final Clock serviceClock;
    private ScheduledFuture<?> scheduledTask;

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void scheduleOnStartup() {
        schedule(admissionScheduleFacade.getCurrentSchedule());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public synchronized void reschedule(AdmissionScheduleChangedEvent event) {
        schedule(admissionScheduleFacade.getSchedule(event.scheduleId()));
    }

    private void schedule(AdmissionSchedule schedule) {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }

        LocalDateTime executionTime = schedule.getSelectFirstPassAt();
        if (!executionTime.isAfter(LocalDateTime.now(serviceClock))) {
            log.info("Skip past first-pass selection schedule for admission year {}",
                    schedule.getAdmissionYear());
            return;
        }

        scheduledTask = admissionTaskScheduler.schedule(
                () -> execute(schedule.getId()),
                executionTime.atZone(TimeConfig.SERVICE_ZONE_ID).toInstant()
        );
        log.info("Scheduled first-pass selection for admission year {} at {}",
                schedule.getAdmissionYear(), executionTime);
    }

    private void execute(Long scheduleId) {
        try {
            executeFirstPassSelectionUseCase.execute(scheduleId);
        } catch (DataIntegrityViolationException e) {
            log.info("First-pass selection was already claimed by another server: {}", scheduleId);
        } catch (Exception e) {
            log.error("Failed to execute first-pass selection: {}", scheduleId, e);
        }
    }
}
