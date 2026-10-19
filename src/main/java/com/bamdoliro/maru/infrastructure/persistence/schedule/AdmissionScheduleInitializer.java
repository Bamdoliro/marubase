package com.bamdoliro.maru.infrastructure.persistence.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdmissionScheduleInitializer implements ApplicationRunner {

    private final AdmissionScheduleRepository admissionScheduleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (admissionScheduleRepository.findByActiveTrue().isPresent()) {
            return;
        }

        var latestSchedule = admissionScheduleRepository.findFirstByOrderByAdmissionYearDesc();
        if (latestSchedule.isPresent()) {
            latestSchedule.get().activate();
            return;
        }

        log.warn("No admission schedule configured. Create a schedule through the admin API.");
    }
}
