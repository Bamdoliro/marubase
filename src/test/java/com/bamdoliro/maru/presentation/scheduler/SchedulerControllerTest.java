package com.bamdoliro.maru.presentation.scheduler;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.scheduler.dto.request.ScheduleRequest;
import com.bamdoliro.maru.presentation.scheduler.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchedulerControllerTest extends RestDocsTestSupport {

    @Test
    void 일차_합격_기간을_설정한다() throws Exception {
        User user = UserFixture.createAdminUser();
        ScheduleRequest request = new ScheduleRequest(LocalDateTime.parse("2025-10-27T15:00:00"));

        given(authenticationArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(scheduleFirstPassUseCase.execute(any(ScheduleRequest.class)))
                .willReturn(new ScheduleResponse(request.getScheduleSelectFirstPass()));

        mockMvc.perform(post("/schedule/first-pass")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("scheduleSelectFirstPass")
                                        .type(JsonFieldType.STRING)
                                        .description("설정할 일차 합격자 선발 스케줄")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.scheduleSelectFirstPass").description("설정된 일차 합격자 선발 스케줄 시간")
                        )
                ));
    }
}
