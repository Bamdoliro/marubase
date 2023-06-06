package com.bamdoliro.maru.domain.form.domain.value;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Attendance {

    private Integer absenceCount;

    private Integer latenessCount;

    private Integer earlyLeaveCount;

    private Integer classAbsenceCount;
}
