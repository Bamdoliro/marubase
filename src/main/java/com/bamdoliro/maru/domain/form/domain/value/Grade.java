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
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_certificate",
            joinColumns = @JoinColumn(name = "form_id"))
    @Column(nullable = false, name = "certificate")
    @Enumerated(EnumType.STRING)
    private List<Certificate> certificateList;

    public Grade(SubjectList subjectList, Attendance attendance1, Attendance attendance2, Attendance attendance3, Integer volunteerTime1, Integer volunteerTime2, Integer volunteerTime3, List<Certificate> certificateList) {
        this.subjectList = subjectList;
        this.attendance1 = attendance1;
        this.attendance2 = attendance2;
        this.attendance3 = attendance3;
        this.volunteerTime1 = volunteerTime1;
        this.volunteerTime2 = volunteerTime2;
        this.volunteerTime3 = volunteerTime3;
        this.certificateList = validate(certificateList);
    }

    private List<Certificate> validate(List<Certificate> certificateList) {
        if (Objects.nonNull(certificateList) && !certificateList.isEmpty()) {
            Optional<Certificate> highestScoreComputerSpecialistCertificate = certificateList.stream()
                    .filter(Certificate::isComputerSpecialist)
                    .max(Comparator.comparingInt(Certificate::getScore));

            if (highestScoreComputerSpecialistCertificate.isPresent()) {
                List<Certificate> validatedCertificateList = new ArrayList<>(
                        certificateList.stream()
                                .filter((c) -> !c.isComputerSpecialist())
                                .toList());

                validatedCertificateList.add(highestScoreComputerSpecialistCertificate.get());
                return validatedCertificateList;
            }
        }

        return certificateList;
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
}
