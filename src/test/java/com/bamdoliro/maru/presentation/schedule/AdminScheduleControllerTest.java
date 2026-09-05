package com.bamdoliro.maru.presentation.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.schedule.dto.request.ScheduleRequest;
import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticatedUser;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminScheduleControllerTest extends RestDocsTestSupport {

    @Test
    void 현재_입학_일정을_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class)))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(AuthenticatedUser.from(user));
        given(queryCurrentScheduleUseCase.execute()).willReturn(new ScheduleResponse(schedule));

        mockMvc.perform(get("/admin/schedules/current")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken").description("액세스 토큰")
                        )
                ));

        verify(queryCurrentScheduleUseCase).execute();
    }

    @Test
    void 현재_입학_일정을_수정한다() throws Exception {
        User user = UserFixture.createAdminUser();
        ScheduleRequest request = ScheduleFixture.createScheduleRequest(null);
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class)))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(AuthenticatedUser.from(user));
        willDoNothing().given(updateScheduleUseCase).execute(any(), any());

        mockMvc.perform(put("/admin/schedules/current")
                        .cookie(AuthFixture.createAuthCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken").description("액세스 토큰")
                        )
                ));

        verify(updateScheduleUseCase).execute(any(), any());
    }
}
