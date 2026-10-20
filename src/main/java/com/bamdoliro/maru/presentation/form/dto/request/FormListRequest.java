package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormListRequest {

    private FormStatus status;
    private FormType type;
    private String sort;
    private Boolean mentoringProgram;

    @Size(max = 100, message = "학교명은 100자 이하여야 합니다.")
    private String schoolName;

    @PositiveOrZero
    private Double firstRoundScoreMin;

    @PositiveOrZero
    private Double firstRoundScoreMax;

    @PositiveOrZero
    private Double totalScoreMin;

    @PositiveOrZero
    private Double totalScoreMax;

    @AssertTrue(message = "점수 범위는 유한한 숫자여야 하며 최솟값이 최댓값보다 클 수 없습니다.")
    public boolean isScoreRangeValid() {
        return validRange(firstRoundScoreMin, firstRoundScoreMax)
                && validRange(totalScoreMin, totalScoreMax);
    }

    private boolean validRange(Double min, Double max) {
        return (min == null || Double.isFinite(min))
                && (max == null || Double.isFinite(max))
                && (min == null || max == null || min <= max);
    }
}
