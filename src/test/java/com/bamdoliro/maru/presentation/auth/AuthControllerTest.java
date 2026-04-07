package com.bamdoliro.maru.presentation.auth;

import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends RestDocsTestSupport {

    @Test
    void 유저가_로그인한다() throws Exception {
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getPhoneNumber(), "비밀번호");
        TokenResponse response = new TokenResponse(AuthFixture.createAccessTokenString(), AuthFixture.createRefreshTokenString());

        given(logInUseCase.execute(any(LogInRequest.class))).willReturn(response);

        mockMvc.perform(post("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("전화번호"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("비밀번호")
                        )
                ));
    }

    @Test
    void 유저가_로그인할_때_유저가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getPhoneNumber(), "비밀번호");
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
        LogInRequest request = new LogInRequest(user.getPhoneNumber(), "비밀번호");
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

    @Test
    void 리프레시_토큰으로_액세스_토큰을_재발급한다() throws Exception {
        String refreshToken = AuthFixture.createRefreshTokenString();
        TokenResponse response = TokenResponse.builder().accessToken(AuthFixture.createAccessTokenString()).build();
        given(refreshTokenUseCase.execute(refreshToken)).willReturn(response);

        mockMvc.perform(patch("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Refresh-Token", refreshToken)
                )

                .andExpect(status().is2xxSuccessful())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Refresh-Token")
                                        .description("리프레시 토큰")
                        )
                ));
    }

    @Test
    void 만료된_리프레시_토큰으로_액세스_토큰_재발급을_요청하면_에러가_발생한다() throws Exception {
        String expiredRefreshToken = AuthFixture.createRefreshTokenString();
        doThrow(new ExpiredTokenException()).when(refreshTokenUseCase).execute(expiredRefreshToken);

        mockMvc.perform(patch("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Refresh-Token", expiredRefreshToken)
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());
    }

    @Test
    void 액세스_토큰으로_액세스_토큰_재발급을_요청하면_에러가_발생한다() throws Exception {
        String accessToken = AuthFixture.createRefreshTokenString();
        doThrow(new InvalidTokenException()).when(refreshTokenUseCase).execute(accessToken);

        mockMvc.perform(patch("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Refresh-Token", accessToken)
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());
    }

    @Test
    void 액세스_토큰을_재발급할_때_리프레시_토큰을_보내지_않으면_에러가_발생한다() throws Exception {

        mockMvc.perform(patch("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(refreshTokenUseCase, never()).execute(anyString());
    }

    @Test
    void 토큰의_서명_알고리즘이_불일치하면_에러가_발생한다() throws Exception {
        String accessToken = AuthFixture.createAccessTokenString();
        doThrow(new InvalidTokenException()).when(refreshTokenUseCase).execute(accessToken);

        mockMvc.perform(patch("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Refresh-Token", accessToken)
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());
    }

    @Test
    void 유저가_로그아웃한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(logOutUseCase).execute(user);

        mockMvc.perform(delete("/auth")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(logOutUseCase, times(1)).execute(user);
    }
}