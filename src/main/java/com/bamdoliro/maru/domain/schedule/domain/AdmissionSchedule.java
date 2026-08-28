package com.bamdoliro.maru.domain.schedule.domain;

import com.bamdoliro.maru.domain.schedule.exception.InvalidSchedulePeriodException;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_admission_schedule")
@Entity
public class AdmissionSchedule extends BaseTimeEntity {

    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer admissionYear;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime applicationFormStart;

    @Column(nullable = false)
    private LocalDateTime applicationFormEnd;

    @Column(nullable = false)
    private LocalDateTime selectFirstPassAt;

    @Column(nullable = false)
    private LocalDateTime announcementOfFirstPass;

    @Column(nullable = false)
    private LocalDateTime announcementOfSecondPass;

    @Column(nullable = false)
    private LocalDateTime codingTest;

    @Column(nullable = false)
    private LocalDateTime ncs;

    @Column(nullable = false)
    private LocalDateTime depthInterview;

    @Column(nullable = false)
    private LocalDateTime physicalExamination;

    @Column(nullable = false)
    private LocalDateTime entranceRegistrationPeriodStart;

    @Column(nullable = false)
    private LocalDateTime entranceRegistrationPeriodEnd;

    @Column(nullable = false)
    private LocalDateTime meisterTalentEntranceTime;

    @Column(nullable = false)
    private LocalDateTime meisterTalentExclusionEntranceTime;

    @Column(nullable = false)
    private LocalDateTime admissionAndPledgeStart;

    @Column(nullable = false)
    private LocalDateTime admissionAndPledgeEnd;

    @Version
    private Long version;

    @Builder
    public AdmissionSchedule(
            Integer admissionYear,
            boolean active,
            LocalDateTime applicationFormStart,
            LocalDateTime applicationFormEnd,
            LocalDateTime selectFirstPassAt,
            LocalDateTime announcementOfFirstPass,
            LocalDateTime announcementOfSecondPass,
            LocalDateTime codingTest,
            LocalDateTime ncs,
            LocalDateTime depthInterview,
            LocalDateTime physicalExamination,
            LocalDateTime entranceRegistrationPeriodStart,
            LocalDateTime entranceRegistrationPeriodEnd,
            LocalDateTime meisterTalentEntranceTime,
            LocalDateTime meisterTalentExclusionEntranceTime,
            LocalDateTime admissionAndPledgeStart,
            LocalDateTime admissionAndPledgeEnd
    ) {
        validate(
                admissionYear,
                applicationFormStart,
                applicationFormEnd,
                selectFirstPassAt,
                announcementOfFirstPass,
                announcementOfSecondPass,
                codingTest,
                ncs,
                depthInterview,
                physicalExamination,
                entranceRegistrationPeriodStart,
                entranceRegistrationPeriodEnd,
                meisterTalentEntranceTime,
                meisterTalentExclusionEntranceTime,
                admissionAndPledgeStart,
                admissionAndPledgeEnd
        );
        this.admissionYear = admissionYear;
        this.active = active;
        this.applicationFormStart = applicationFormStart;
        this.applicationFormEnd = applicationFormEnd;
        this.selectFirstPassAt = selectFirstPassAt;
        this.announcementOfFirstPass = announcementOfFirstPass;
        this.announcementOfSecondPass = announcementOfSecondPass;
        this.codingTest = codingTest;
        this.ncs = ncs;
        this.depthInterview = depthInterview;
        this.physicalExamination = physicalExamination;
        this.entranceRegistrationPeriodStart = entranceRegistrationPeriodStart;
        this.entranceRegistrationPeriodEnd = entranceRegistrationPeriodEnd;
        this.meisterTalentEntranceTime = meisterTalentEntranceTime;
        this.meisterTalentExclusionEntranceTime = meisterTalentExclusionEntranceTime;
        this.admissionAndPledgeStart = admissionAndPledgeStart;
        this.admissionAndPledgeEnd = admissionAndPledgeEnd;
    }

    public void update(
            Integer admissionYear,
            LocalDateTime applicationFormStart,
            LocalDateTime applicationFormEnd,
            LocalDateTime selectFirstPassAt,
            LocalDateTime announcementOfFirstPass,
            LocalDateTime announcementOfSecondPass,
            LocalDateTime codingTest,
            LocalDateTime ncs,
            LocalDateTime depthInterview,
            LocalDateTime physicalExamination,
            LocalDateTime entranceRegistrationPeriodStart,
            LocalDateTime entranceRegistrationPeriodEnd,
            LocalDateTime meisterTalentEntranceTime,
            LocalDateTime meisterTalentExclusionEntranceTime,
            LocalDateTime admissionAndPledgeStart,
            LocalDateTime admissionAndPledgeEnd
    ) {
        validate(
                admissionYear,
                applicationFormStart,
                applicationFormEnd,
                selectFirstPassAt,
                announcementOfFirstPass,
                announcementOfSecondPass,
                codingTest,
                ncs,
                depthInterview,
                physicalExamination,
                entranceRegistrationPeriodStart,
                entranceRegistrationPeriodEnd,
                meisterTalentEntranceTime,
                meisterTalentExclusionEntranceTime,
                admissionAndPledgeStart,
                admissionAndPledgeEnd
        );
        this.admissionYear = admissionYear;
        this.applicationFormStart = applicationFormStart;
        this.applicationFormEnd = applicationFormEnd;
        this.selectFirstPassAt = selectFirstPassAt;
        this.announcementOfFirstPass = announcementOfFirstPass;
        this.announcementOfSecondPass = announcementOfSecondPass;
        this.codingTest = codingTest;
        this.ncs = ncs;
        this.depthInterview = depthInterview;
        this.physicalExamination = physicalExamination;
        this.entranceRegistrationPeriodStart = entranceRegistrationPeriodStart;
        this.entranceRegistrationPeriodEnd = entranceRegistrationPeriodEnd;
        this.meisterTalentEntranceTime = meisterTalentEntranceTime;
        this.meisterTalentExclusionEntranceTime = meisterTalentExclusionEntranceTime;
        this.admissionAndPledgeStart = admissionAndPledgeStart;
        this.admissionAndPledgeEnd = admissionAndPledgeEnd;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private void validate(
            Integer admissionYear,
            LocalDateTime applicationFormStart,
            LocalDateTime applicationFormEnd,
            LocalDateTime selectFirstPassAt,
            LocalDateTime announcementOfFirstPass,
            LocalDateTime announcementOfSecondPass,
            LocalDateTime codingTest,
            LocalDateTime ncs,
            LocalDateTime depthInterview,
            LocalDateTime physicalExamination,
            LocalDateTime entranceRegistrationPeriodStart,
            LocalDateTime entranceRegistrationPeriodEnd,
            LocalDateTime meisterTalentEntranceTime,
            LocalDateTime meisterTalentExclusionEntranceTime,
            LocalDateTime admissionAndPledgeStart,
            LocalDateTime admissionAndPledgeEnd
    ) {
        boolean invalid = !allNotNull(
                admissionYear,
                applicationFormStart,
                applicationFormEnd,
                selectFirstPassAt,
                announcementOfFirstPass,
                announcementOfSecondPass,
                codingTest,
                ncs,
                depthInterview,
                physicalExamination,
                entranceRegistrationPeriodStart,
                entranceRegistrationPeriodEnd,
                meisterTalentEntranceTime,
                meisterTalentExclusionEntranceTime,
                admissionAndPledgeStart,
                admissionAndPledgeEnd
        );
        if (invalid) {
            throw new InvalidSchedulePeriodException();
        }

        invalid = !isBefore(applicationFormStart, applicationFormEnd)
                || !isBefore(applicationFormEnd, selectFirstPassAt)
                || selectFirstPassAt.isAfter(announcementOfFirstPass)
                || !isBefore(announcementOfFirstPass, meisterTalentEntranceTime)
                || !isBefore(announcementOfFirstPass, meisterTalentExclusionEntranceTime)
                || meisterTalentEntranceTime.isAfter(codingTest)
                || meisterTalentExclusionEntranceTime.isAfter(ncs)
                || !isBefore(codingTest, ncs)
                || !isBefore(ncs, depthInterview)
                || !isBefore(depthInterview, physicalExamination)
                || !isBefore(physicalExamination, announcementOfSecondPass)
                || !isBefore(announcementOfSecondPass, entranceRegistrationPeriodStart)
                || !isBefore(entranceRegistrationPeriodStart, entranceRegistrationPeriodEnd)
                || admissionAndPledgeStart.isBefore(entranceRegistrationPeriodStart)
                || !isBefore(admissionAndPledgeStart, admissionAndPledgeEnd)
                || admissionAndPledgeEnd.isAfter(entranceRegistrationPeriodEnd);

        if (invalid) {
            throw new InvalidSchedulePeriodException();
        }
    }

    private boolean isBefore(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null && start.isBefore(end);
    }

    private boolean allNotNull(Object... values) {
        for (Object value : values) {
            if (Objects.isNull(value)) {
                return false;
            }
        }
        return true;
    }
}
