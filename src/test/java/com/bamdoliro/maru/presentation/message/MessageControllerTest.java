package com.bamdoliro.maru.presentation.message;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MessageControllerTest extends RestDocsTestSupport {

    @MockBean
    private FormRepository formRepository;

    @BeforeEach
    void setup(){
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.receive();
        List<Form> forms = Arrays.asList(form);

        given(formRepository.findByStatus(FormStatus.RECEIVED)).willReturn(forms);
    }

    @Test
    void 원서가_접수된_학생들에게_메시지를_보낸다() throws Exception{

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "배고파요..", FormStatus.RECEIVED);
        doNothing().when(sendMessageService).execute(any(SendMessageRequest.class));

        mockMvc.perform(post("/message")
                .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("문자 메시지 제목"),
                                fieldWithPath("text")
                                        .type(JsonFieldType.STRING)
                                        .description("문자 메시지 내용"),
                                fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("메시지를 보낼 원서의 상태")
                        )
                ));

        verify(sendMessageService, times(1)).execute(any(SendMessageRequest.class));
    }

    @Test
    void 해당제출상태인_원서가_없으면_오류가난다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "배고파요..", FormStatus.FINAL_SUBMITTED);

        // 예외 발생 설정
        doThrow(new RuntimeException("원서를 찾을 수 없음")).when(sendMessageService).execute(any(SendMessageRequest.class));

        mockMvc.perform(post("/message")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isInternalServerError()) // 내부 서버 오류 예상
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

        verify(sendMessageService, times(1)).execute(any(SendMessageRequest.class));
    }




}
