package com.bamdoliro.maru.infrastructure.school;

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
class QueryAllMiddleSchoolServiceTest {

    @Autowired
    private QueryAllMiddleSchoolService queryAllMiddleSchoolService;

    @Test
    void 부산소프트웨어를_검색하면_부산소프트웨어마이스터고등학교를_반환한다() throws JsonProcessingException {
        List<SchoolResponse> responseList = queryAllMiddleSchoolService.execute();
        System.out.println(responseList);
    }

    @Test
    void 검색_결과가_많다면_상위_10개만_반환한다() throws JsonProcessingException {
        List<SchoolResponse> responseList = queryAllMiddleSchoolService.execute();
        assertEquals(10, responseList.size());
    }

    @Test
    void 검색_결과가_없다면_빈_리스트를_반환한다() throws JsonProcessingException {
        List<SchoolResponse> responseList = queryAllMiddleSchoolService.execute();
        assertEquals(0, responseList.size());
    }
}