package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends RestDocsTestSupport {

    @MockBean
    private SignUpUserUseCase signUpUserUseCase;

    @Test
    void 유저를_생성한다() throws Exception {
        willDoNothing().given(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("maru@bamdoliro.com", "password123$");

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
                                fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("최소 8 자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자를 포함하는 비밀번호")
                        )
                ));
    }


    @Test
    void 유저를_생성할_때_이미_유저가_있다면_에러가_발생한다() throws Exception {
        doThrow(new UserAlreadyExistsException())
                .when(signUpUserUseCase).execute(any(SignUpUserRequest.class));
        SignUpUserRequest request = new SignUpUserRequest("maru@bamdoliro.com", "password123$");

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
        SignUpUserRequest request = new SignUpUserRequest("bamdoliro", "p$");

        mockMvc.perform(post("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());
    }
}