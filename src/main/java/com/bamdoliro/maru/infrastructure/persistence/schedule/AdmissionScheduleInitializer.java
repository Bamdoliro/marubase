package com.bamdoliro.maru.infrastructure.persistence.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

        admissionScheduleRepository.save(
                AdmissionSchedule.builder()
                        .admissionYear(2026)
                        .active(true)
                        .applicationFormStart(LocalDateTime.of(2025, 1, 20, 9, 0))
                        .applicationFormEnd(LocalDateTime.of(2025, 10, 23, 17, 0))
                        .selectFirstPassAt(LocalDateTime.of(2025, 10, 23, 18, 0))
                        .announcementOfFirstPass(LocalDateTime.of(2025, 10, 27, 15, 0))
                        .announcementOfSecondPass(LocalDateTime.of(2025, 11, 5, 15, 0))
                        .codingTest(LocalDateTime.of(2025, 10, 31, 9, 30))
                        .ncs(LocalDateTime.of(2025, 10, 31, 11, 0))
                        .depthInterview(LocalDateTime.of(2025, 10, 31, 13, 0))
                        .physicalExamination(LocalDateTime.of(2025, 10, 31, 15, 0))
                        .entranceRegistrationPeriodStart(LocalDateTime.of(2025, 11, 16, 0, 0))
                        .entranceRegistrationPeriodEnd(LocalDateTime.of(2025, 12, 18, 0, 0))
                        .meisterTalentEntranceTime(LocalDateTime.of(2025, 10, 31, 9, 0))
                        .meisterTalentExclusionEntranceTime(LocalDateTime.of(2025, 10, 31, 10, 30))
                        .admissionAndPledgeStart(LocalDateTime.of(2025, 12, 15, 0, 0))
                        .admissionAndPledgeEnd(LocalDateTime.of(2025, 12, 17, 23, 59, 59))
                        .build()
        );
    }
}
