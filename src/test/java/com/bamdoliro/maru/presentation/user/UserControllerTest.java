package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.infrastructure.mail.exception.FailedToSendMailException;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends RestDocsTestSupport {

    @Test
    void 유저를_생성한다() throws Exception {
        willDoNothing().given(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("maru@bamdoliro.com", "김밤돌", "ABC123", "password123$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email")
                                        .type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("이메일 인증 코드"),
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("최소 8 자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자를 포함하는 비밀번호")
                        )
                ));
    }

    @Test
    void 유저를_생성할_때_인증하지_않은_이메일이거나_만료된_이메일이라면_에러가_발생한다() throws Exception {
        doThrow(new VerifyingHasFailedException()).when(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("maru@bamdoliro.com", "김밤돌", "ABC123", "password123$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isUnauthorized())
                .andDo(restDocs.document());
    }

    @Test
    void 유저를_생성할_때_인증_코드가_틀렸으면_에러가_발생한다() throws Exception {
        doThrow(new VerificationCodeMismatchException()).when(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("maru@bamdoliro.com", "김밤돌", "ABC123", "password123$");

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
        SignUpUserRequest request = new SignUpUserRequest("maru@bamdoliro.com", "김밤돌", "ABC123", "password123$");

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
        SignUpUserRequest request = new SignUpUserRequest("bamdoliro", "", "ABC13", "p$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());
    }

    @Test
    void 이메일_인증을_요청한다() throws Exception {
        willDoNothing().given(sendEmailVerificationUseCase).execute(any(String.class));

        mockMvc.perform(post("/user/verification?email=maru@bamdoliro.com")
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().is2xxSuccessful())

                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("email")
                                        .description("이메일")
                        )
                ));
    }

    @Test
    void 이메일_인증을_요청할_때_잘못된_형식의_이메일을_보내면_에러가_발생한다() throws Exception {
        willDoNothing().given(sendEmailVerificationUseCase).execute(any(String.class));

        mockMvc.perform(post("/user/verification?email=누가봐도이메일아님")
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());
    }

    @Test
    void 이메일_인증을_요청할_때_이메일_전송이_실패하면_에러가_발생한다() throws Exception {
        doThrow(new FailedToSendMailException())
                .when(sendEmailVerificationUseCase).execute(anyString());

        mockMvc.perform(post("/user/verification?email=maru@bamdoliro.com")
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isInternalServerError())

                .andDo(restDocs.document());
    }
}