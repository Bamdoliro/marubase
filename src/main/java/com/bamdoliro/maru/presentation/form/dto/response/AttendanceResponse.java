package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceResponse {

    private Integer absenceCount;
    private Integer latenessCount;
    private Integer earlyLeaveCount;
    private Integer classAbsenceCount;

    public AttendanceResponse(Attendance attendance) {
        this.absenceCount = attendance.getAbsenceCount();
        this.latenessCount = attendance.getLatenessCount();
        this.earlyLeaveCount = attendance.getEarlyLeaveCount();
        this.classAbsenceCount = attendance.getClassAbsenceCount();
    }
}
