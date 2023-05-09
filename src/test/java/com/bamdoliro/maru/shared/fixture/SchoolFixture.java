package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;

import java.util.List;

public class SchoolFixture {

    public static List<SchoolResponse> createSchoolListResponse() {
        return List.of(
                new SchoolResponse("부산소프트웨어마이스터고등학교", "부산광역시", "7150658")
        );
    }

    public static List<SchoolResponse> createSchoolMaxListResponse() {
        return List.of(
                new SchoolResponse("비전고등학교", "경기도", "7531109"),
                new SchoolResponse("비전중학교", "경기도", "7631003"),
                new SchoolResponse("비전초등학교", "경기도", "7631034"),
                new SchoolResponse("공동체비전고등학교", "충청남도", "8140222"),
                new SchoolResponse("다른비전고등학교", "경상북도", "7531109"),
                new SchoolResponse("다른비전중학교", "경기도", "7631003"),
                new SchoolResponse("다른비전초등학교", "경상남도", "7631034"),
                new SchoolResponse("다른공동체비전고등학교", "충청남도", "8140222"),
                new SchoolResponse("또다른비전고등학교", "대구광역시", "7531109"),
                new SchoolResponse("또다른비전중학교", "경기도", "7631003")
        );
    }

    public static List<SchoolResponse> createEmptySchoolListResponse() {
        return List.of();
    }
}
