package com.bamdoliro.maru.domain.schedule.domain;

import com.bamdoliro.maru.domain.schedule.exception.InvalidSchedulePeriodException;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
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
                        schedule.getAdmissionAndPledgeEnd()
                )
        );
    }
}
