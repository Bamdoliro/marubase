package com.bamdoliro.maru.shared.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil {

    public static Double roundTo(double value, int place) {
        String format = "%." + place + "f";
        return Double.parseDouble(String.format(format, value));
    }

    public static Double round(Double value){
        if(value == null){
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
