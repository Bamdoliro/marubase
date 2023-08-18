package com.bamdoliro.maru.shared.constants;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class Schedule {

    public  final LocalDateTime START = LocalDateTime.of(2023, 10, 16, 9, 0);
    public static final LocalDateTime END = LocalDateTime.of(2023, 10, 19, 17, 0);
    public static final LocalDateTime ANNOUNCEMENT_OF_FIRST_PASS = LocalDateTime.of(2023, 10, 23, 15, 0);
    public static final LocalDateTime ANNOUNCEMENT_OF_SECOND_PASS = LocalDateTime.of(2023, 11, 2, 15, 0);

    public static final String SELECT_FIRST_PASS_CRON = "0 0 1 20 10 ? 2023";
}
