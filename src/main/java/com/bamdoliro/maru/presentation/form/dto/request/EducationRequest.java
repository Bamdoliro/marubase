package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.form.domain.value.Education;
import com.bamdoliro.maru.domain.form.domain.value.PhoneNumber;
import com.bamdoliro.maru.domain.form.domain.value.School;
import com.bamdoliro.maru.domain.form.domain.value.Teacher;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EducationRequest {

    @NotNull(message = "필수값입니다.")
    private GraduationType graduationType;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 4, max = 4, message = "4자여야 합니다.")
    private String graduationYear;

    @Nullable
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String schoolName;

    @Nullable
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String schoolLocation;

    @Nullable
    @Size(max = 40, message = "40자 이하여야 합니다.")
    private String schoolAddress;

    @Nullable
    @Size(min = 7, max = 7, message = "7자여야 합니다.")
    private String schoolCode;

    @Nullable
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String teacherName;

    @Nullable
    @Size(max = 11, message = "11자 이하여야 합니다.")
    private String teacherPhoneNumber;

    @Nullable
    @Size(max = 11, message = "11자 이하여야 합니다.")
    private String teacherMobilePhoneNumber;

    public Education toValue() {
        return new Education(
                graduationType,
                graduationYear,
                new School(
                        schoolName,
                        schoolLocation,
                        schoolAddress,
                        schoolCode
                ),
                new Teacher(
                        teacherName,
                        new PhoneNumber(teacherPhoneNumber),
                        new PhoneNumber(teacherMobilePhoneNumber)
                )
        );
    }
}