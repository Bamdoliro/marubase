package com.bamdoliro.maru.domain.policy;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record SelectionPolicy(
        @NotNull @Min(0) @Max(1000) Integer regular,
        @NotNull @Min(0) @Max(1000) Integer meisterTalent,
        @NotNull @Min(0) @Max(1000) Integer socialIntegration,
        @NotNull @Min(0) @Max(1000) Integer nationalVeteransEducation,
        @NotNull @Min(0) @Max(1000) Integer specialAdmission,
        @NotNull @DecimalMin("1") @DecimalMax("5") @Digits(integer = 1, fraction = 3) BigDecimal firstRoundMultiplier,
        @NotNull @DecimalMin("0") @DecimalMax("1") @Digits(integer = 1, fraction = 4) BigDecimal otherRegionRate
) {
    public static SelectionPolicy defaults() {
        return new SelectionPolicy(48, 10, 6, 1, 1, new BigDecimal("1.3"), new BigDecimal("0.5"));
    }

    public int total() {
        return regular + meisterTalent + socialIntegration;
    }

    public int firstRoundCount(int count) {
        return BigDecimal.valueOf(count).multiply(firstRoundMultiplier)
                .setScale(0, RoundingMode.CEILING).intValueExact();
    }

    public int otherRegionCount(boolean firstRound) {
        int totalCount = firstRound ? firstRoundCount(total()) : total();
        return BigDecimal.valueOf(totalCount).multiply(otherRegionRate)
                .setScale(0, firstRound ? RoundingMode.FLOOR : RoundingMode.CEILING)
                .intValueExact();
    }
}
