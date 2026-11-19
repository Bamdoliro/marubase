package com.bamdoliro.maru.shared.constants;

import lombok.experimental.UtilityClass;

/** 정책 분리 전 선발 결과를 고정하는 회귀 테스트 기준값. */
@UtilityClass
public class FixedNumber {

    public static final int TOTAL = 64;
    public static final int REGULAR = 48;
    public static final int MEISTER_TALENT = 10;
    public static final int SOCIAL_INTEGRATION = 6;
    public static final int NATIONAL_VETERANS_EDUCATION = 1;
    public static final int SPECIAL_ADMISSION = 1;
    public static final double MULTIPLE = 1.3;
    public static final double OTHER_REGION_RATE = 0.5;
}
