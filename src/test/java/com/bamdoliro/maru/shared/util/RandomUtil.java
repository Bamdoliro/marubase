package com.bamdoliro.maru.shared.util;

public class RandomUtil {

    public static String randomPhoneNumber() {
        return "010" + randomNumber(1000, 9999) + randomNumber(1000, 9999);
    }

    public static int randomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static double randomDouble(double min, double max) {
        return Math.round(((Math.random() * (max - min)) + min) * 1000.0) / 1000.0;
    }
}
