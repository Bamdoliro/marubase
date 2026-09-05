package com.bamdoliro.maru.presentation.schedule.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    @NotNull(message = "필수값입니다.")
    @Min(value = 2000, message = "2000 이상이어야 합니다.")
    private Integer admissionYear;

    private Long version;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime applicationFormStart;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime applicationFormEnd;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime selectFirstPassAt;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime announcementOfFirstPass;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime announcementOfSecondPass;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime codingTest;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime ncs;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime depthInterview;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime physicalExamination;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime entranceRegistrationPeriodStart;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime entranceRegistrationPeriodEnd;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime meisterTalentEntranceTime;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime meisterTalentExclusionEntranceTime;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime admissionAndPledgeStart;

    @NotNull(message = "필수값입니다.")
    private LocalDateTime admissionAndPledgeEnd;
}
