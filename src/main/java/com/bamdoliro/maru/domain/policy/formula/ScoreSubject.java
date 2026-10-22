package com.bamdoliro.maru.domain.policy.formula;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** 미리보기 요청의 원시 과목. 운영 원서 엔티티와 연결하지 않는다. */
public record ScoreSubject(
        @NotBlank @Size(max = 40) String id,
        @Min(1) @Max(3) Integer grade,
        @Min(1) @Max(2) Integer semester,
        @NotBlank @Size(max = 40) String name,
        @Pattern(regexp = "[A-F]") String achievement,
        @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 4) BigDecimal originalScore
) {
}
