package com.bamdoliro.maru.shared.util;

public class MathUtil {

    public static Double roundTo(double value, int place) {
        return Double.parseDouble(String.format("%." + place + "f", value));
    }
}
