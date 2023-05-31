package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.application.question.UpdateQuestionUseCase;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.security.auth.AuthDetails;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionControllerTest extends RestDocsTestSupport {

    @Test
    void 자주묻는질문을_생성한다() throws Exception {
        willDoNothing().given(createQuestionUseCase).execute(any(CreateQuestionRequest.class));
        CreateQuestionRequest request = new CreateQuestionRequest("오늘 급식 맛있엇나용?", "토요일인데요");

        User user = UserFixture.createUser();
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

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
                                        .description("1024글자 이내의 내용")
                        )
                ));
    }

    @Test
    void 자주묻는질문을_수정한다() throws Exception {
        willDoNothing().given(createQuestionUseCase).execute(any(CreateQuestionRequest.class));
        UpdateQuestionRequest request = new UpdateQuestionRequest("이거 맞나", "아님 말고...");

        User user = UserFixture.createUser();
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

        long id = 1;
        mockMvc.perform(put("/question/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(parameterWithName("id").description("질문 ID")),
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
    void 자주묻는질문을_수정할_때_자주묻는질문이_없으면_에러가_발생한다() throws Exception {

        UpdateQuestionRequest request = new UpdateQuestionRequest("이거 맞나", "아님 말고...");
        willThrow(new QuestionNotFoundException()).given(updateQuestionUseCase).execute(eq(1L), any(UpdateQuestionRequest.class));

        User user = UserFixture.createUser();
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

        long id = 1;
        mockMvc.perform(put("/question/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());
    }

}