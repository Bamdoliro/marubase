package com.bamdoliro.maru.application.school;

import com.bamdoliro.maru.infrastructure.neis.SearchSchoolService;
import com.bamdoliro.maru.presentation.school.dto.response.SchoolResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SearchSchoolUseCaseTest {

    @InjectMocks
    private SearchSchoolUseCase searchSchoolUseCase;

    @Mock
    private SearchSchoolService searchSchoolService;

    @Test
    void 학교를_검색한다() throws JsonProcessingException {
        // given
        String q = "부산소프트웨어";
        SchoolResponse schoolResponse = SchoolResponse.builder()
                .name("부산소프트웨어마이스터고등학교")
                .build();

        given(searchSchoolService.execute(q)).willReturn(List.of(schoolResponse));

        // when
        List<SchoolResponse> response = searchSchoolUseCase.execute(q);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1, response.size()),
                () -> assertEquals("부산소프트웨어마이스터고등학교", response.get(0).getName())
        );
    }
}