package com.bamdoliro.maru.shared.constants;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class Schedule {

    public static final LocalDateTime APPLICATION_FORM_START = LocalDateTime.of(2025, 1, 20, 9, 0);
    //원서 접수 시작 시간
    public static final LocalDateTime APPLICATION_FORM_END = LocalDateTime.of(2025, 10, 23, 17, 0);
    //원서 접수 마감 시간
    public static final LocalDateTime ANNOUNCEMENT_OF_FIRST_PASS = LocalDateTime.of(2025, 10, 27, 15, 0);
    //일차 합격자 발표 시간
    public static final LocalDateTime ANNOUNCEMENT_OF_SECOND_PASS = LocalDateTime.of(2025, 11, 5, 15, 0);
    //이차 합격자 발표 시간
    public static final LocalDateTime CODING_TEST = LocalDateTime.of(2025, 10, 31, 9, 30);
    //코딩테스트 시작시간
    public static final LocalDateTime NCS = LocalDateTime.of(2025, 10, 31, 11, 0);
    //NCS 테스트 시작시간
    public static final LocalDateTime DEPTH_INTERVIEW = LocalDateTime.of(2025, 10, 31, 13, 0);
    //면접 시작시간
    public static final LocalDateTime PHYSICAL_EXAMINATION = LocalDateTime.of(2025, 10, 31,  15, 0);
    //건강검사 시작시간
    public static final LocalDateTime ENTRANCE_REGISTRATION_PERIOD_START = LocalDateTime.of(2025, 11, 16, 0, 0);
    //입학 등록 기간 시작시간
    public static final LocalDateTime ENTRANCE_REGISTRATION_PERIOD_END = LocalDateTime.of(2025, 12, 18, 0, 0);
    //입학 등록 마감 시간
    public static final LocalDateTime MEISTER_TALENT_ENTRANCE_TIME = LocalDateTime.of(2025, 10, 31, 9, 0);
    //마이스터 인재전형 시험장 입실시간
    public static final LocalDateTime MEISTER_TALENT_EXCLUSION_ENTRANCE_TIME = LocalDateTime.of(2025, 10, 31, 10, 30);
    //일반, 사회통합 전형 시험장 입실시간
    public static final LocalDateTime ADMISSION_AND_PLEDGE_START = LocalDateTime.of(2025, 12, 15, 0, 0);
    //서약서 업로드?? 시작??
    public static final LocalDateTime ADMISSION_AND_PLEDGE_END = LocalDateTime.of(2025, 12, 17, 23, 59);
    //서약서 업로드?? 끝?
    //두개는 헷갈리네요.. 죄송
    public static final String SELECT_FIRST_PASS_CRON = "0 0 18 23 10 ?";

    public static int getAdmissionYear() {
        return APPLICATION_FORM_START.plusYears(1L).getYear();
    }

    public String toLocaleString(LocalDateTime datetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E) HH:mm", Locale.KOREA);
        return formatter.format(datetime);
    }

    public String toLocaleString(LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter startTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREA);
        DateTimeFormatter endTimeFormatter = DateTimeFormatter.ofPattern(" ~ MM.dd (E)", Locale.KOREA);
        return startTimeFormatter.format(startTime) + endTimeFormatter.format(endTime);
    }
}
