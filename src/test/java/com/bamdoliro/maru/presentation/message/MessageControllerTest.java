package com.bamdoliro.maru.presentation.message;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByStatusRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MessageControllerTest extends RestDocsTestSupport {

    @Test
    void 조회할_상태의_원서를_가진_학생들에게() throws Exception {
        User user = UserFixture.createUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageByStatusRequest request = new SendMessageByStatusRequest("부산소마고 공지사항", "테스트임니다...", FormStatus.FINAL_SUBMITTED);
        willDoNothing().given(sendMessageUseCase).execute(request);

        mockMvc.perform(post("/message/status")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("문자 메시지 제목"),
                                fieldWithPath("text").type(JsonFieldType.STRING).description("문자 메시지 내용"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("메시지를 보낼 원서의 상태")
                        )
                ));
    }

    @Test
    void 해당제출상태인_원서가_없으면_오류가난다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageByStatusRequest request = new SendMessageByStatusRequest("부산소마고 공지사항", "배고파요..", FormStatus.FINAL_SUBMITTED);

        willThrow(new RuntimeException("원서를 찾을 수 없음")).given(sendMessageUseCase).execute(any(SendMessageByStatusRequest.class));

        mockMvc.perform(post("/message/status")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isInternalServerError())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("문자 메시지 제목"),
                                fieldWithPath("text").type(JsonFieldType.STRING).description("문자 메시지 내용"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("메시지를 보낼 원서의 상태")
                        )
                ));
    }
}
