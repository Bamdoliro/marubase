package com.bamdoliro.maru.presentation.message;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByStatusRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByTypeRequest;
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

    @Test
    void 마이스터전형과_마이스터에서_일반전형으로_바뀐_합격자를_제외한_1차_합격자들에게_메시지를_보낸다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageByTypeRequest request = new SendMessageByTypeRequest("부산소마고 공지사항", "추카추카", FormType.REGULAR, false);
        willDoNothing().given(sendMessageUseCase).execute(request);

        mockMvc.perform(post("/message/type")
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
                                fieldWithPath("formType").type(JsonFieldType.STRING).description("메시지를 보낼 원서 전형(MEISTER_TALENT, REGULAR)"),
                                fieldWithPath("isChangeToRegular").type(JsonFieldType.BOOLEAN).description("만약 마이스터 -> 일반 전형이면 true 아니면 false")
                        )
                ));
    }

    @Test
    void 마이스터_전형_1차_합격자들에게_메시지를_보낸다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageByTypeRequest request = new SendMessageByTypeRequest("부산소마고 공지사항", "추카추카", FormType.MEISTER_TALENT, false);
        willDoNothing().given(sendMessageUseCase).execute(request);

        mockMvc.perform(post("/message/type")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isNoContent())

                .andDo(restDocs.document());
    }

    @Test
    void 마이스터전형에서_일반전형으로_바뀐_1차_합격자들에게_메시지를_보낸다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageByTypeRequest request = new SendMessageByTypeRequest("부산소마고 공지사항", "추카추카", FormType.REGULAR, true);
        willDoNothing().given(sendMessageUseCase).execute(request);

        mockMvc.perform(post("/message/type")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isNoContent())

                .andDo(restDocs.document());
    }

    @Test
    void 해당전형의_원서가_없다면_오류가_발생한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageByTypeRequest request = new SendMessageByTypeRequest("부산소마고 공지사항", "오류가 발생하겠죠?", FormType.REGULAR, false);

        willThrow(new RuntimeException("원서를 찾을 수 없음")).given(sendMessageUseCase).execute(any(SendMessageByTypeRequest.class));

        mockMvc.perform(post("/message/type")
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
                                fieldWithPath("formType").type(JsonFieldType.STRING).description("메시지를 보낼 원서 전형(MEISTER_TALENT, REGULAR)"),
                                fieldWithPath("isChangeToRegular").type(JsonFieldType.BOOLEAN).description("만약 마이스터 -> 일반 전형이면 true 아니면 false")
                        )
                ));
    }
}
