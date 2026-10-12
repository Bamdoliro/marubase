package com.bamdoliro.maru.presentation.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScheduleControllerTest extends RestDocsTestSupport {

    @Test
    void 현재_입학_일정을_조회한다() throws Exception {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(queryCurrentScheduleUseCase.execute()).willReturn(new ScheduleResponse(schedule));

        mockMvc.perform(get("/schedules/current")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.applicationFormStart").exists())
                .andExpect(jsonPath("$.data.applicationFormEnd").exists())
                .andDo(restDocs.document());

        verify(queryCurrentScheduleUseCase).execute();
    }
}
