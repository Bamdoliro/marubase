package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.domain.notice.exception.NoticeNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends RestDocsTestSupport {

    @Test
    void 공지사항을_생성한다() throws Exception {
        NoticeRequest request = new NoticeRequest("오늘 급식 맛있엇나용?", "토요일인데요");
        given(createNoticeUseCase.execute(any(NoticeRequest.class))).willReturn(SharedFixture.createIdResponse());

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(post("/notice")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("64글자 이내의 제목"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("1024글자 이내의 내용")
                        )
                ));

        verify(createNoticeUseCase, times(1)).execute(any(NoticeRequest.class));
    }

    @Test
    void 공지사항을_수정한다() throws Exception {
        Long id = 1L;
        NoticeRequest request = new NoticeRequest("이거 맞나", "아님 말고...");
        willDoNothing().given(updateNoticeUseCase).execute(id, request);

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(put("/notice/{notice-id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("notice-id")
                                        .description("공지사항 id")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("64글자 이내의 제목"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("1024글자 이내의 내용")
                        )
                ));
    }

    @Test
    void 공지사항을_수정할_때_공지사항이_없으면_에러가_발생한다() throws Exception {
        Long id = 1L;
        NoticeRequest request = new NoticeRequest("이거 맞나", "아님 말고...");
        willThrow(new NoticeNotFoundException()).given(updateNoticeUseCase).execute(eq(1L), any(NoticeRequest.class));

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(put("/notice/{notice-id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }

    @Test
    void 전체_공지사항을_불러온다() throws Exception {
        List<NoticeSimpleResponse> response = List.of(
                NoticeFixture.createNoticeSimpleResponse(),
                NoticeFixture.createNoticeSimpleResponse(),
                NoticeFixture.createNoticeSimpleResponse()
        );
        given(queryNoticeListUseCase.execute()).willReturn(response);

        mockMvc.perform(get("/notice")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document());

        verify(queryNoticeListUseCase, times(1)).execute();
    }

    @Test
    void 공지사항을_불러온다() throws Exception {
        Long id = 1L;
        NoticeResponse response = new NoticeResponse(NoticeFixture.createNotice());
        given(queryNoticeUseCase.execute(id)).willReturn(response);

        mockMvc.perform(get("/notice/{notice-id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("notice-id")
                                        .description("공지사항 id")
                        )
                ));

        verify(queryNoticeUseCase, times(1)).execute(id);
    }

    @Test
    void 공지사항을_불러올_때_공지사항이_없으면_에러가_발생한다() throws Exception {
        Long id = 1L;
        willThrow(new NoticeNotFoundException()).given(queryNoticeUseCase).execute(id);

        mockMvc.perform(get("/notice/{notice-id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(queryNoticeUseCase, times(1)).execute(id);
    }

    @Test
    void 공지사항을_삭제한다() throws Exception {
        Long id = 1L;
        willDoNothing().given(deleteNoticeUseCase).execute(id);

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(delete("/notice/{notice-id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("notice-id")
                                        .description("공지사항 id")
                        )
                ));

        verify(deleteNoticeUseCase, times(1)).execute(id);
    }
}