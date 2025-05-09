package com.bamdoliro.maru.application.schedular;

import com.bamdoliro.maru.infrastructure.scheduler.FormScheduler;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@UseCase
public class ScheduleFirstPassUseCase {

    private final FormScheduler formScheduler;

    public LocalDateTime execute(LocalDateTime scheduledTime) {
        formScheduler.selectFirstPass(scheduledTime);
        return scheduledTime;
    }
}
