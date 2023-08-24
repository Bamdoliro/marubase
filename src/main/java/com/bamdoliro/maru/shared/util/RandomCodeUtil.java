package com.bamdoliro.maru.shared.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomCodeUtil {

    public static String generate(int count) {
        return RandomStringUtils.randomNumeric(count);
    }
}