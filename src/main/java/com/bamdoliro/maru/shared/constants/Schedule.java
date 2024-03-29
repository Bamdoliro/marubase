package com.bamdoliro.maru.shared.constants;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class Schedule {

    public static final LocalDateTime START = LocalDateTime.of(2023, 10, 16, 9, 0);
    public static final LocalDateTime END = LocalDateTime.of(2023, 10, 19, 17, 0);
    public static final LocalDateTime ANNOUNCEMENT_OF_FIRST_PASS = LocalDateTime.of(2023, 10, 23, 15, 0);
    public static final LocalDateTime ANNOUNCEMENT_OF_SECOND_PASS = LocalDateTime.of(2023, 11, 2, 15, 0);
    public static final LocalDateTime CODING_TEST = LocalDateTime.of(2023, 10, 27, 9, 30);
    public static final LocalDateTime NCS = LocalDateTime.of(2023, 10, 27, 11, 0);
    public static final LocalDateTime DEPTH_INTERVIEW = LocalDateTime.of(2023, 10, 27, 13, 0);
    public static final LocalDateTime PHYSICAL_EXAMINATION = LocalDateTime.of(2023, 10, 27,  13, 0);
    public static final String SELECT_FIRST_PASS_CRON = "0 0 1 20 10 ?";

    public static int getAdmissionYear() {
        return START.plusYears(1L).getYear();
    }

    public String toLocaleString(LocalDateTime datetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E) HH:mm", Locale.KOREA);
        return formatter.format(datetime);
    }
}
