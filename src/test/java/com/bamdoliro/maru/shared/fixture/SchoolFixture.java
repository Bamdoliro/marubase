package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;

import java.util.List;

public class SchoolFixture {

    public static List<SchoolResponse> createSchoolListResponse() {
        return List.of(
                new SchoolResponse("부산소프트웨어마이스터고등학교", "부산광역시", "부산광역시 강서구 가락대로 1393", "7150658")
);
}

public static List<SchoolResponse> createSchoolMaxListResponse() {
return List.of(
new SchoolResponse("비전고등학교", "경기도", "경기도 비전시 비전구 비전로 2", "7531109"),
new SchoolResponse("비전중학교", "경기도","경기도 비전시 비전구 비전로 1", "7631003"),
new SchoolResponse("비전초등학교", "경기도", "경기도 비전시 비전구 비전로 3", "7631034"),
new SchoolResponse("공동체비전고등학교", "충청남도", "충청남도 비전시 비전구 비전로 1", "8140222"),
new SchoolResponse("다른비전고등학교", "경상북도", "경상북도 비전시 비전구 비전로 1", "7531109"),
new SchoolResponse("다른비전중학교", "경기도", "경기도 비전시 비전구 비전로 4", "7631003"),
new SchoolResponse("다른비전초등학교", "경상남도", "경상남도 비전시 비전구 비전로 1", "7631034"),
new SchoolResponse("다른공동체비전고등학교", "충청남도", "충청남도 비전시 비전구 비전로 2", "8140222"),
new SchoolResponse("또다른비전고등학교", "대구광역시", "대구광역시 비전구 비전로 1", "7531109"),
new SchoolResponse("또다른비전중학교", "경기도", "경기도 비전시 비전구 비전로 5", "7631003")
);
}
}
