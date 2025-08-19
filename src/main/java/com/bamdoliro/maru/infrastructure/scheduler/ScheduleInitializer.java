package com.bamdoliro.maru.infrastructure.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleInitializer {

    private final FormScheduler formScheduler;

    @PostConstruct
    public void init() {
        formScheduler.restoreSchedule();
    }
}

