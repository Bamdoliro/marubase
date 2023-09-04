package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.value.CertificateList;
import com.bamdoliro.maru.domain.form.domain.value.Grade;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    public Grade toValue() {
        List<Subject> subjectList = new ArrayList<>();
        this.subjectList.stream()
                .map(SubjectRequest::toValue)
                .forEach(subjectList::addAll);

        return new Grade(
                new SubjectList(subjectList),
                attendance1 == null ? null : attendance1.toValue(),
                attendance2 == null ? null : attendance2.toValue(),
                attendance3 == null ? null : attendance3.toValue(),
                volunteerTime1,
                volunteerTime2,
                volunteerTime3,
                new CertificateList(certificateList)
        );
    }
}
