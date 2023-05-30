package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {

    @NotNull(message = "필수값입니다.")
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer absenceCount;

    @NotNull(message = "필수값입니다.")
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer latenessCount;

    @NotNull(message = "필수값입니다.")
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer earlyLeaveCount;

    @NotNull(message = "필수값입니다.")
    @Min(value = 0, message = "0 이상이여야 합니다.")
    private Integer classAbsenceCount;

    public Attendance toValue() {
        return new Attendance(
                absenceCount,
                latenessCount,
                earlyLeaveCount,
                classAbsenceCount
        );
    }
}
