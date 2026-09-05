package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.exception.ScheduleNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdmissionScheduleFacade {

    private final AdmissionScheduleRepository admissionScheduleRepository;

    public AdmissionSchedule getCurrentSchedule() {
        return admissionScheduleRepository.findByActiveTrue()
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public AdmissionSchedule getSchedule(Integer admissionYear) {
        return admissionScheduleRepository.findByAdmissionYear(admissionYear)
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public AdmissionSchedule getSchedule(Long id) {
        return admissionScheduleRepository.findById(id)
                .orElseThrow(ScheduleNotFoundException::new);
    }
}
