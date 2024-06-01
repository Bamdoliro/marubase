package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends RestDocsTestSupport {

    @Test
    void 유저를_생성한다() throws Exception {
        willDoNothing().given(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest(
                "01085852525",
                "김밤돌",
                "password123$"
        );

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("전화번호"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("최소 8 자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자를 포함하는 비밀번호")
                        )
                ));
    }

    @Test
    void 유저를_생성할_때_전화번호_인증이_실패한_경우_에러가_발생한다() throws Exception {
        doThrow(new VerifyingHasFailedException()).when(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("01085852525", "김밤돌", "password123$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isUnauthorized())
                .andDo(restDocs.document());
    }

    @Test
    void 유저를_생성할_때_이미_유저가_있다면_에러가_발생한다() throws Exception {
        doThrow(new UserAlreadyExistsException())
                .when(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("01085852525", "김밤돌", "password123$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().is(UserErrorProperty.USER_ALREADY_EXISTS.getStatus().value()))

                .andDo(restDocs.document());
    }

    @Test
    void 유저를_생성할_때_잘못된_형식의_요청을_보내면_에러가_발생한다() throws Exception {
        willDoNothing().given(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("bamdoliro", "", "p$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());
    }

    @Test
    void 전화번호_인증을_요청한다() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest("01085852525");
        willDoNothing().given(sendVerificationUseCase).execute(any(SendVerificationRequest.class));

        mockMvc.perform(post("/user/verification")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("전화번호")
                        )
                ));

        verify(sendVerificationUseCase, times(1)).execute(any(SendVerificationRequest.class));
    }

    @Test
    void 전화번호_인증을_요청할_때_잘못된_형식의_전화번호를_보내면_에러가_발생한다() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest("누가봐도전화번호아님ㅎg");
        willDoNothing().given(sendVerificationUseCase).execute(any(SendVerificationRequest.class));

        mockMvc.perform(post("/user/verification")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(sendVerificationUseCase, never()).execute(any(SendVerificationRequest.class));
    }

    @Test
    void 전화번호_인증을_요청할_때_전화번호_전송이_실패하면_에러가_발생한다() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest("010아무도안쓰는번호");
        doThrow(new FailedToSendException())
                .when(sendVerificationUseCase).execute(any(SendVerificationRequest.class));

        mockMvc.perform(post("/user/verification")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isInternalServerError())

                .andDo(restDocs.document());

        verify(sendVerificationUseCase, times(1)).execute(any(SendVerificationRequest.class));
    }

    @Test
    void 로그인한_유저를_불러온다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(get("/user")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));
    }

    @Test
    void 전화번호_인증을_완료한다() throws Exception {
        willDoNothing().given(verifyUseCase).execute(any(VerifyRequest.class));
        VerifyRequest request = new VerifyRequest("01085852525", "123456");

        mockMvc.perform(patch("/user/verification")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("전화번호"),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("인증 코드")
                        )
                ));

        verify(verifyUseCase, times(1)).execute(any(VerifyRequest.class));
    }

    @Test
    void 전화번호를_인증할_때_인증_코드가_틀렸으면_에러가_발생한다() throws Exception {
        doThrow(new VerificationCodeMismatchException()).when(verifyUseCase).execute(any(VerifyRequest.class));
        VerifyRequest request = new VerifyRequest("01085852525", "123456");

        mockMvc.perform(patch("/user/verification")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());

        verify(verifyUseCase, times(1)).execute(any(VerifyRequest.class));
    }

    @Test
    void 전화번호를_인증할_때_인증이_실패한_경우_에러가_발생한다() throws Exception {
        doThrow(new VerifyingHasFailedException()).when(verifyUseCase).execute(any(VerifyRequest.class));
        VerifyRequest request = new VerifyRequest("01085852525", "123456");

        mockMvc.perform(patch("/user/verification")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());

        verify(verifyUseCase, times(1)).execute(any(VerifyRequest.class));
    }

    @Test
    void 비밀번호를_변경한다() throws Exception {
        willDoNothing().given(updatePasswordUseCase).execute(any(UpdatePasswordRequest.class));
        UpdatePasswordRequest request = new UpdatePasswordRequest("01085852525", "hihi1234!");

        mockMvc.perform(put("/user/password-update")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("전화번호"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("변경할 비밀번호")
                        )
                ));
    }

    @Test
    void 비밀번호를_변경할_때_전화번호_인증이_실패한_경우_에러가_발생한다() throws Exception {
        doThrow(new VerifyingHasFailedException()).when(updatePasswordUseCase).execute(any(UpdatePasswordRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("01085852525", "김밤돌", "password123$");

        mockMvc.perform(put("/user/password-update")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isUnauthorized())
                .andDo(restDocs.document());
    }

    @Test
    void 해당_전화번호를_가진_유저가_없으면_오류가_발생한다() throws Exception {
        doThrow(new UserNotFoundException()).when(updatePasswordUseCase).execute(any(UpdatePasswordRequest.class));
        UpdatePasswordRequest request = new UpdatePasswordRequest("01085852525", "hihi1234!");

        mockMvc.perform(put("/user/password-update")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(updatePasswordUseCase, times(1)).execute(any(UpdatePasswordRequest.class));
    }

    @Test
    void 비밀번호_형식이_다르면_오류가_발생한다() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("01085852525", "123456");

        mockMvc.perform(put("/user/password-update")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(updatePasswordUseCase, never()).execute(any(UpdatePasswordRequest.class));
    }
}