package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.response.FairDetailResponse;
import com.bamdoliro.maru.presentation.fair.dto.response.FairResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FairFixture {

    public static Fair createFair() {
        return new Fair(
                LocalDateTime.of(2025, 4, 15, 10, 39),
                120,
                "서울시 강남구",
                FairType.STUDENT_AND_PARENT,
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );
    }
    public static CreateFairRequest createFairRequest() {
        return new CreateFairRequest(
                LocalDateTime.of(2025, 4, 15, 10, 39),
                120,
                "서울시 강남구",
                FairType.STUDENT_AND_PARENT,
                LocalDate.now(),
                null
        );
    }

    public static Fair createClosedFair() {
        return new Fair(
                LocalDateTime.of(2005, 4, 15, 10, 39),
                120,
                "서울시 강남구",
                FairType.STUDENT_AND_PARENT,
                LocalDate.now(),
                LocalDate.now().plusDays(3)
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

    public static List<Fair> createFairList() {
        return List.of(
                createFair(),
                createFair(),
                createFair()
        );
    }

    public static List<FairResponse> createFairResponseList() {
        return List.of(
                new FairResponse(createClosedFair(), null),
                new FairResponse(createClosedFair(), null),
                new FairResponse(createClosedFair(), null)
        );
    }

    public static Fair createFairDetail() {
        Fair fair = createClosedFair();
        List<Attendee> attendeeList = List.of(
                createAttendee(fair),
                createAttendee(fair),
                createAttendee(fair)
        );
        fair.getAttendeeList().addAll(attendeeList);

        return fair;
    }

    public static FairDetailResponse createFairDetailResponse() {
        return new FairDetailResponse(createFairDetail(), null);
    }
}
