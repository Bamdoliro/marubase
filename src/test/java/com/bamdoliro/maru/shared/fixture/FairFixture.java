package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;

import java.time.LocalDateTime;

public class FairFixture {

    public static Fair createFair() {
        return new Fair(
                LocalDateTime.of(2005, 4, 15, 10, 39),
                120,
                "서울시 강남구",
                FairType.STUDENT_AND_PARENT
        );
    }
    public static CreateFairRequest createFairRequest() {
        return new CreateFairRequest(
                LocalDateTime.of(2005, 4, 15, 10, 39),
                120,
                "서울시 강남구",
                FairType.STUDENT_AND_PARENT
        );
    }

    public static AttendAdmissionFairRequest createAttendAdmissionFairRequest() {
        return new AttendAdmissionFairRequest(
                "비전중학교",
                "곰밤돌",
                "학생",
                "01012344321",
                2,
                "내신 커트라인 몇인가요?"
        );
    }

    public static Attendee createAttendee(Fair fair) {
        return new Attendee(
                "비전중학교",
                "곰밤돌",
                "학생",
                "01012344321",
                2,
                "내신 커트라인 몇인가요?",
                fair
        );
    }
}
