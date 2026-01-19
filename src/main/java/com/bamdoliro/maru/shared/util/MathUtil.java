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
        return BigDecimal.valueOf(value)
                .setScale(place, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
