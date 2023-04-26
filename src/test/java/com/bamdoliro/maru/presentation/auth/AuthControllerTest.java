package com.bamdoliro.maru.presentation.auth;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends RestDocsTestSupport {

    @Test
    void 유저가_로그인한다() throws Exception {
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getEmail(), "비밀번호");
        TokenResponse response = new TokenResponse(AuthFixture.createAccessTokenString(), AuthFixture.createRefreshTokenString());
        given(logInUseCase.execute(any(LogInRequest.class))).willReturn(response);

        mockMvc.perform(post("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().is2xxSuccessful())

                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email")
                                        .type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("비밀번호")
                        )
                ));
    }

    @Test
    void 유저가_로그인할_때_유저가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getEmail(), "비밀번호");
        doThrow(new UserNotFoundException()).when(logInUseCase).execute(any(LogInRequest.class));

        mockMvc.perform(post("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }

    @Test
    void 유저가_로그인할_때_비밀번호가_틀리면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getEmail(), "비밀번호");
        doThrow(new PasswordMismatchException()).when(logInUseCase).execute(any(LogInRequest.class));

        mockMvc.perform(post("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());
    }

    @Test
    void 유저가_로그인할_때_잘못된_형식의_요청을_보내면_에러가_발생한다() throws Exception {
        LogInRequest request = new LogInRequest("", "");

        mockMvc.perform(post("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(logInUseCase, never()).execute(any(LogInRequest.class));
    }
}