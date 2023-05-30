package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Grade {

    @Embedded
    private SubjectList subjectList;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "absenceCount", column = @Column(name = "attendance1_absence_count")),
            @AttributeOverride(name = "latenessCount", column = @Column(name = "attendance1_lateness_count")),
            @AttributeOverride(name = "earlyLeaveCount", column = @Column(name = "attendance1_early_leave_count")),
            @AttributeOverride(name = "classAbsenceCount", column = @Column(name = "attendance1_class_absence_count"))
    })
    private Attendance attendance1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "absenceCount", column = @Column(name = "attendance2_absence_count")),
            @AttributeOverride(name = "latenessCount", column = @Column(name = "attendance2_lateness_count")),
            @AttributeOverride(name = "earlyLeaveCount", column = @Column(name = "attendance2_early_leave_count")),
            @AttributeOverride(name = "classAbsenceCount", column = @Column(name = "attendance2_class_absence_count"))
    })
    private Attendance attendance2;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "absenceCount", column = @Column(name = "attendance3_absence_count")),
            @AttributeOverride(name = "latenessCount", column = @Column(name = "attendance3_lateness_count")),
            @AttributeOverride(name = "earlyLeaveCount", column = @Column(name = "attendance3_early_leave_count")),
            @AttributeOverride(name = "classAbsenceCount", column = @Column(name = "attendance3_class_absence_count"))
    })
    private Attendance attendance3;

    @Column(nullable = true)
    private Integer volunteerTime1;

    @Column(nullable = true)
    private Integer volunteerTime2;

    @Column(nullable = true)
    private Integer volunteerTime3;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_certificate",
            joinColumns = @JoinColumn(name = "form_id"))
    @Column(nullable = false, name = "certificate")
    @Enumerated(EnumType.STRING)
    private List<Certificate> certificateList;

    public Attendance getTotalAttendance() {
        return new Attendance(
                attendance1.getAbsenceCount() + attendance2.getAbsenceCount() + attendance3.getAbsenceCount(),
                attendance1.getLatenessCount() + attendance2.getLatenessCount() + attendance3.getLatenessCount(),
                attendance1.getEarlyLeaveCount() + attendance2.getEarlyLeaveCount() + attendance3.getEarlyLeaveCount(),
                attendance1.getClassAbsenceCount() + attendance2.getClassAbsenceCount() + attendance3.getClassAbsenceCount()
        );
    }

    public Integer getTotalVolunteerTime() {
        return volunteerTime1 + volunteerTime2 + volunteerTime3;
    }

    public List<Subject> getSubjectListValue() {
        return subjectList.getValue();
    }
}
