package com.bamdoliro.maru.domain.schedule.domain;

import com.bamdoliro.maru.domain.schedule.exception.InvalidSchedulePeriodException;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AdmissionScheduleTest {

    @Test
    void 원서_접수_시작이_종료보다_늦으면_에러가_발생한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();

        assertThrows(
                InvalidSchedulePeriodException.class,
                () -> schedule.update(
                        schedule.getAdmissionYear(),
                        schedule.getApplicationFormEnd(),
                        schedule.getApplicationFormStart(),
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
                        schedule.getAdmissionAndPledgeEnd(),
                        schedule.getMaintenanceStart(),
                        schedule.getMaintenanceEnd()
                )
        );
    }

    @Test
    void 점검_시작과_종료_중_하나만_있으면_에러가_발생한다() {
        LocalDateTime maintenanceStart = LocalDateTime.of(2025, 10, 19, 22, 0);

        assertThrows(
                InvalidSchedulePeriodException.class,
                () -> ScheduleFixture.createSchedule(maintenanceStart, null)
        );
    }

    @Test
    void 점검_시작이_종료보다_늦으면_에러가_발생한다() {
        LocalDateTime maintenanceStart = LocalDateTime.of(2025, 10, 20, 1, 0);
        LocalDateTime maintenanceEnd = LocalDateTime.of(2025, 10, 20, 0, 0);

        assertThrows(
                InvalidSchedulePeriodException.class,
                () -> ScheduleFixture.createSchedule(maintenanceStart, maintenanceEnd)
        );
    }
}
