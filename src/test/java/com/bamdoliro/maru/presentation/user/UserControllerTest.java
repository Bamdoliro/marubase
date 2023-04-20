package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends RestDocsTestSupport {

    @MockBean
    private SignUpUserUseCase signUpUserUseCase;

    @WithAnonymousUser
    @Test
    void 회원가입() throws Exception {
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
}