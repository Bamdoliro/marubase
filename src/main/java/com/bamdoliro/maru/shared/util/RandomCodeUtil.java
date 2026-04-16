package com.bamdoliro.maru.shared.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class RandomCodeUtil {

    public static String generate(int count) {
        return RandomStringUtils.randomNumeric(count);
    }
}