package com.bamdoliro.maru.shared.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil {

    public static Double roundTo(Double value, int place){
        if(value == null){
            return null;
        }
        if(value.isNaN() || value.isInfinite()){
            throw new IllegalArgumentException("점수 계산 결과가 NaN 또는 Infinite입니다: " + value);
        }
        return BigDecimal.valueOf(value)
                .setScale(place, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
