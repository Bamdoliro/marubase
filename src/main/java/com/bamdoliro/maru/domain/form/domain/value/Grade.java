package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Embedded
    private CertificateList certificateList;

    public Grade(SubjectList subjectList, Attendance attendance1, Attendance attendance2, Attendance attendance3, Integer volunteerTime1, Integer volunteerTime2, Integer volunteerTime3, CertificateList certificateList) {
        this.subjectList = subjectList;
        this.attendance1 = attendance1;
        this.attendance2 = attendance2;
        this.attendance3 = attendance3;
        this.volunteerTime1 = volunteerTime1;
        this.volunteerTime2 = volunteerTime2;
        this.volunteerTime3 = volunteerTime3;
        this.certificateList = certificateList;
    }

    public Attendance getTotalAttendance() {
        return new Attendance(
                attendance1.getAbsenceCount() + attendance2.getAbsenceCount() + attendance3.getAbsenceCount(),
                attendance1.getLatenessCount() + attendance2.getLatenessCount() + attendance3.getLatenessCount(),
                attendance1.getEarlyLeaveCount() + attendance2.getEarlyLeaveCount() + attendance3.getEarlyLeaveCount(),
                attendance1.getClassAbsenceCount() + attendance2.getClassAbsenceCount() + attendance3.getClassAbsenceCount()
        );
    }

    public Integer getTotalAbsenceCount() {
        return attendance1.getAbsenceCount() + attendance2.getAbsenceCount() + attendance3.getAbsenceCount();
    }

    public Integer getTotalLatenessCount() {
        return attendance1.getLatenessCount() + attendance2.getLatenessCount() + attendance3.getLatenessCount();
    }

    public Integer getTotalEarlyLeaveCount() {
        return attendance1.getEarlyLeaveCount() + attendance2.getEarlyLeaveCount() + attendance3.getEarlyLeaveCount();
    }

    public Integer getTotalClassAbsenceCount() {
        return attendance1.getClassAbsenceCount() + attendance2.getClassAbsenceCount() + attendance3.getClassAbsenceCount();
    }

    public Integer getTotalVolunteerTime() {
        return volunteerTime1 + volunteerTime2 + volunteerTime3;
    }

    public List<Subject> getSubjectListValue() {
        return subjectList.getValue();
    }

    public List<Certificate> getCertificateListValue() {
        return certificateList.getValue();
    }
}
