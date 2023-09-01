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
    void 비전을_검색하면_비전중학교를_반환한다() throws JsonProcessingException {
        String q = "비전";
        List<SchoolResponse> responseList = searchSchoolService.execute(q);
        assertEquals(1, responseList.size());
        assertEquals("비전중학교", responseList.get(0).getName());
    }

    @Test
    void 검색_결과가_많다면_상위_10개만_반환한다() throws JsonProcessingException {
        String q = "가";
        List<SchoolResponse> responseList = searchSchoolService.execute(q);
        assertEquals(10, responseList.size());
    }

    @Test
    void 검색_결과가_없다면_빈_리스트를_반환한다() throws JsonProcessingException {
        String q = "누가봐도없을것같은검색어";
        List<SchoolResponse> responseList = searchSchoolService.execute(q);
        assertEquals(0, responseList.size());
    }
}