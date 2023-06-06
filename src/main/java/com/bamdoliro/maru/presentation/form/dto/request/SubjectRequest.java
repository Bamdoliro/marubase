package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {

    @NotNull(message = "필수값입니다.")
    @Min(value = 1, message = "1 이상이여야 합니다.")
    @Max(value = 3, message = "3 이하여야 합니다.")
    private Integer grade;

    @NotNull(message = "필수값입니다.")
    @Min(value = 1, message = "1 이상이여야 합니다.")
    @Max(value = 2, message = "2 이하여야 합니다.")
    private Integer semester;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 15, message = "15자 이하여야 합니다.")
    private String subjectName;

    @NotNull(message = "필수값입니다.")
    private AchievementLevel achievementLevel;

    public Subject toValue() {
        return new Subject(
                grade,
                semester,
                subjectName,
                achievementLevel
        );
    }
}
