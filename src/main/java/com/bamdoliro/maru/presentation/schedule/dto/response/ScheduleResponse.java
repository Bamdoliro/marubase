package com.bamdoliro.maru.presentation.schedule.dto.response;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ScheduleResponse {

    private final Long id;
    private final Integer admissionYear;
    private final boolean active;
    private final Long version;
    private final LocalDateTime applicationFormStart;
    private final LocalDateTime applicationFormEnd;
    private final LocalDateTime selectFirstPassAt;
    private final LocalDateTime announcementOfFirstPass;
    private final LocalDateTime announcementOfSecondPass;
    private final LocalDateTime codingTest;
    private final LocalDateTime ncs;
    private final LocalDateTime depthInterview;
    private final LocalDateTime physicalExamination;
    private final LocalDateTime entranceRegistrationPeriodStart;
    private final LocalDateTime entranceRegistrationPeriodEnd;
    private final LocalDateTime meisterTalentEntranceTime;
    private final LocalDateTime meisterTalentExclusionEntranceTime;
    private final LocalDateTime admissionAndPledgeStart;
    private final LocalDateTime admissionAndPledgeEnd;
    private final LocalDateTime updatedAt;

    public ScheduleResponse(AdmissionSchedule schedule) {
        this.id = schedule.getId();
        this.admissionYear = schedule.getAdmissionYear();
        this.active = schedule.isActive();
        this.version = schedule.getVersion();
        this.applicationFormStart = schedule.getApplicationFormStart();
        this.applicationFormEnd = schedule.getApplicationFormEnd();
        this.selectFirstPassAt = schedule.getSelectFirstPassAt();
        this.announcementOfFirstPass = schedule.getAnnouncementOfFirstPass();
        this.announcementOfSecondPass = schedule.getAnnouncementOfSecondPass();
        this.codingTest = schedule.getCodingTest();
        this.ncs = schedule.getNcs();
        this.depthInterview = schedule.getDepthInterview();
        this.physicalExamination = schedule.getPhysicalExamination();
        this.entranceRegistrationPeriodStart = schedule.getEntranceRegistrationPeriodStart();
        this.entranceRegistrationPeriodEnd = schedule.getEntranceRegistrationPeriodEnd();
        this.meisterTalentEntranceTime = schedule.getMeisterTalentEntranceTime();
        this.meisterTalentExclusionEntranceTime = schedule.getMeisterTalentExclusionEntranceTime();
        this.admissionAndPledgeStart = schedule.getAdmissionAndPledgeStart();
        this.admissionAndPledgeEnd = schedule.getAdmissionAndPledgeEnd();
        this.updatedAt = schedule.getUpdatedAt();
    }
}
