package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.presentation.schedule.dto.request.ScheduleRequest;
import java.time.LocalDateTime;

public class ScheduleFixture {

    public static AdmissionSchedule createSchedule() {
        return AdmissionSchedule.builder()
                .admissionYear(2026)
                .active(true)
                .applicationFormStart(LocalDateTime.of(2025, 10, 20, 9, 0))
                .applicationFormEnd(LocalDateTime.of(2025, 10, 23, 17, 0))
                .selectFirstPassAt(LocalDateTime.of(2025, 10, 23, 18, 0))
                .announcementOfFirstPass(LocalDateTime.of(2025, 10, 27, 15, 0))
                .meisterTalentEntranceTime(LocalDateTime.of(2025, 10, 31, 9, 0))
                .codingTest(LocalDateTime.of(2025, 10, 31, 9, 30))
                .meisterTalentExclusionEntranceTime(LocalDateTime.of(2025, 10, 31, 10, 30))
                .ncs(LocalDateTime.of(2025, 10, 31, 11, 0))
                .depthInterview(LocalDateTime.of(2025, 10, 31, 13, 0))
                .physicalExamination(LocalDateTime.of(2025, 10, 31, 15, 0))
                .announcementOfSecondPass(LocalDateTime.of(2025, 11, 5, 15, 0))
                .entranceRegistrationPeriodStart(LocalDateTime.of(2025, 11, 16, 0, 0))
                .admissionAndPledgeStart(LocalDateTime.of(2025, 12, 15, 0, 0))
                .admissionAndPledgeEnd(LocalDateTime.of(2025, 12, 17, 23, 59, 59))
                .entranceRegistrationPeriodEnd(LocalDateTime.of(2025, 12, 18, 0, 0))
                .build();
    }

    public static ScheduleRequest createScheduleRequest(Long version) {
        AdmissionSchedule schedule = createSchedule();
        return new ScheduleRequest(
                schedule.getAdmissionYear(),
                version,
                schedule.getApplicationFormStart(),
                schedule.getApplicationFormEnd(),
                schedule.getSelectFirstPassAt(),
                schedule.getAnnouncementOfFirstPass(),
                schedule.getAnnouncementOfSecondPass(),
                schedule.getCodingTest(),
                schedule.getNcs(),
                schedule.getDepthInterview(),
                schedule.getPhysicalExamination(),
                schedule.getEntranceRegistrationPeriodStart(),
                schedule.getEntranceRegistrationPeriodEnd(),
                schedule.getMeisterTalentEntranceTime(),
                schedule.getMeisterTalentExclusionEntranceTime(),
                schedule.getAdmissionAndPledgeStart(),
                schedule.getAdmissionAndPledgeEnd()
        );
    }
}
