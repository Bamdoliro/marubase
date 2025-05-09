package com.bamdoliro.maru.infrastructure.scheduler;

import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
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

    private ScheduledFuture<?> scheduledTask;

    public void selectFirstPass(LocalDateTime scheduledTime) {
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("이미 지난 시간입니다");
        }

        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }

        scheduledTask = taskScheduler.schedule(
                selectFirstPassUseCase::execute,
                Timestamp.valueOf(scheduledTime)
        );
    }
}
