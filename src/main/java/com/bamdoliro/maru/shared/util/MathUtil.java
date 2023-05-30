package com.bamdoliro.maru.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

    public static Double roundTo(double value, int place) {
        String format = "%." + place + "f";
        return Double.parseDouble(String.format(format, value));
    }
}
