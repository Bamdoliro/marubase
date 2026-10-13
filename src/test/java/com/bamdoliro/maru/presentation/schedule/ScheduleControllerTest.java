package com.bamdoliro.maru.presentation.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.nullValue;
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
                .andExpect(jsonPath("$.data.maintenanceStart").value(nullValue()))
                .andExpect(jsonPath("$.data.maintenanceEnd").value(nullValue()))
                .andDo(restDocs.document());

        verify(queryCurrentScheduleUseCase).execute();
    }

    @Test
    void 현재_점검_시간을_함께_조회한다() throws Exception {
        LocalDateTime maintenanceStart = LocalDateTime.of(2025, 10, 19, 22, 0);
        LocalDateTime maintenanceEnd = LocalDateTime.of(2025, 10, 20, 1, 0);
        AdmissionSchedule schedule = ScheduleFixture.createSchedule(
                maintenanceStart,
                maintenanceEnd
        );
        given(queryCurrentScheduleUseCase.execute()).willReturn(new ScheduleResponse(schedule));

        mockMvc.perform(get("/schedules/current")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.maintenanceStart")
                        .value("2025-10-19T22:00:00"))
                .andExpect(jsonPath("$.data.maintenanceEnd")
                        .value("2025-10-20T01:00:00"));

        verify(queryCurrentScheduleUseCase).execute();
    }
}
