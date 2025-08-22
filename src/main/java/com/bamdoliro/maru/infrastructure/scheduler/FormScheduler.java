package com.bamdoliro.maru.infrastructure.scheduler;

import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
import com.bamdoliro.maru.domain.scheduler.domain.FirstPassSchedule;
import com.bamdoliro.maru.domain.scheduler.exception.CannotSchedulePastTimeException;
import com.bamdoliro.maru.infrastructure.persistence.scheduler.FirstPassScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Component
public class FormScheduler {

    private final SelectFirstPassUseCase selectFirstPassUseCase;
    private final TaskScheduler taskScheduler;
    private final FirstPassScheduleRepository firstPassScheduleRepository;

    private ScheduledFuture<?> scheduledTask;

    public void selectFirstPass(LocalDateTime scheduledTime) {
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new CannotSchedulePastTimeException();
        }

        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }

        FirstPassSchedule schedule = new FirstPassSchedule(scheduledTime);
        firstPassScheduleRepository.save(schedule);

        scheduledTask = taskScheduler.schedule(
                selectFirstPassUseCase::execute,
                Timestamp.valueOf(scheduledTime)
        );
    }

    public void restoreSchedule() {
        firstPassScheduleRepository.findById(1L)
                .map(FirstPassSchedule::getScheduledTime)
                .filter(time -> time != null && time.isAfter(java.time.LocalDateTime.now()))
                .ifPresent(this::selectFirstPass);
    }
}
