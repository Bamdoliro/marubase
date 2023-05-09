package com.bamdoliro.maru.infrastructure.neis;

import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@SpringBootTest
class SearchSchoolServiceTest {

    @Autowired
    private SearchSchoolService searchSchoolService;

    @Test
    void 부산소프트웨어를_검색하면_부산소프트웨어마이스터고등학교를_반환한다() throws JsonProcessingException {
        String q = "부산소프트웨어";
        List<SchoolResponse> responseList = searchSchoolService.execute(q);
        assertEquals(1, responseList.size());
        assertEquals("부산소프트웨어마이스터고등학교", responseList.get(0).getName());
    }

    @Test
    void 검색_결과가_많다면_상위_10개만_반환한다() throws JsonProcessingException {
        String q = "가";
        List<SchoolResponse> responseList = searchSchoolService.execute(q);
        assertEquals(10, responseList.size());
    }
}