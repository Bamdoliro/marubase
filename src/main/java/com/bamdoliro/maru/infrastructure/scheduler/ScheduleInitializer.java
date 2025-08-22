package com.bamdoliro.maru.infrastructure.scheduler;

import com.bamdoliro.maru.domain.scheduler.domain.FirstPassSchedule;
import com.bamdoliro.maru.infrastructure.persistence.scheduler.FirstPassScheduleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleInitializer {

    private final FormScheduler formScheduler;
    private final FirstPassScheduleRepository firstPassScheduleRepository;

    @PostConstruct
    public void init() {
        if (firstPassScheduleRepository.findTopByOrderByCreatedAtDesc().isEmpty()) {
            firstPassScheduleRepository.save(
                    new FirstPassSchedule(LocalDateTime.parse("2025-10-25T15:00:00"))
            );
        }
        formScheduler.restoreSchedule();
    }
}

