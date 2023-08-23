package com.bamdoliro.maru.presentation.fair;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FairFixture;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FairControllerTest extends RestDocsTestSupport {

    @Test
    void 입학설명회_일정을_만든다() throws Exception {
        CreateFairRequest request = FairFixture.createFairRequest();
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(createAdmissionFairUseCase).execute(request);

        mockMvc.perform(post("/fair")
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
                                fieldWithPath("start")
                                        .type(JsonFieldType.STRING)
                                        .description("입학설명회 시작일 (yyyy-MM-ddThh:mm:ss)"),
                                fieldWithPath("capacity")
                                        .type(JsonFieldType.NUMBER)
                                        .description("입학설명회 정원"),
                                fieldWithPath("place")
                                        .type(JsonFieldType.STRING)
                                        .description("입학설명회 장소"),
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("입학설명회 유형 (STUDENT_AND_PARENT, TEACHER)")
                        )
                ));

        verify(createAdmissionFairUseCase, times(1)).execute(any(CreateFairRequest.class));
    }
}