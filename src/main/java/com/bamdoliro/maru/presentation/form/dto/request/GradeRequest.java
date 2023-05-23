package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GradeRequest {

    @Valid
    private List<SubjectRequest> subjectList;

    @Nullable
    @Valid
    private AttendanceRequest attendance1;

    @Nullable
    @Valid
    private AttendanceRequest attendance2;

    @Nullable
    @Valid
    private AttendanceRequest attendance3;

    @Nullable
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer volunteerTime1;

    @Nullable
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer volunteerTime2;

    @Nullable
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer volunteerTime3;

    @Nullable
    private List<Certificate> certificateList;
}
