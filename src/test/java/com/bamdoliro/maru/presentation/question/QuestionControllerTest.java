package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionControllerTest extends RestDocsTestSupport {

    @Test
    void 자주묻는질문을_생성한다() throws Exception {
        willDoNothing().given(createQuestionUseCase).execute(any(CreateQuestionRequest.class));
        CreateQuestionRequest request = new CreateQuestionRequest("오늘 급식 맛있엇나용?", "토요일인데요", QuestionCategory.TOP_QUESTION);

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(post("/question")
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
                                        .description("1024글자 이내의 내용"),
                                fieldWithPath("category")
                                        .type(JsonFieldType.STRING)
                                        .description("카테고리")
                        )
                ));
    }

    @Test
    void 자주묻는질문을_수정한다() throws Exception {
        willDoNothing().given(createQuestionUseCase).execute(any(CreateQuestionRequest.class));
        UpdateQuestionRequest request = new UpdateQuestionRequest("이거 맞나", "아님 말고...", QuestionCategory.TOP_QUESTION);

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(put("/question/{id}", 1)
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
                        pathParameters(parameterWithName("id").description("자주 묻는 질문 id")),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("64글자 이내의 제목"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("1024글자 이내의 내용"),
                                fieldWithPath("category")
                                        .type(JsonFieldType.STRING)
                                        .description("카테고리")
                        )
                ));
    }

    @Test
    void 자주묻는질문을_수정할_때_자주묻는질문이_없으면_에러가_발생한다() throws Exception {
        Long id = 1L;
        UpdateQuestionRequest request = new UpdateQuestionRequest("이거 맞나", "아님 말고...", QuestionCategory.TOP_QUESTION);
        willThrow(new QuestionNotFoundException()).given(updateQuestionUseCase).execute(eq(1L), any(UpdateQuestionRequest.class));

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(put("/question/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }

    @Test
    void 자주묻는질문을_불러온다() throws Exception {
        List<QuestionResponse> response = List.of(
                QuestionFixture.createQuestionResponse(),
                QuestionFixture.createQuestionResponse(),
                QuestionFixture.createQuestionResponse()
        );
        given(queryQuestionListUseCase.execute(QuestionCategory.TOP_QUESTION)).willReturn(response);

        mockMvc.perform(get("/question")
                        .param("category", QuestionCategory.TOP_QUESTION.name())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("category")
                                        .description("카테고리")
                        )
                ));

        verify(queryQuestionListUseCase, times(1)).execute(QuestionCategory.TOP_QUESTION);
    }
}