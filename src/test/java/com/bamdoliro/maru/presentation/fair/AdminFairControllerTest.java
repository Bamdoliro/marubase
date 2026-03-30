package com.bamdoliro.maru.presentation.fair;

import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminFairControllerTest extends RestDocsTestSupport {

    @Test
    void 입학설명회_일정을_만든다() throws Exception {
        CreateFairRequest request = FairFixture.createFairRequest();
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(createAdmissionFairUseCase).execute(request);

        mockMvc.perform(post("/admin/fairs")
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
                                        .description("입학설명회 일시 (yyyy-MM-ddThh:mm:ss)"),
                                fieldWithPath("capacity")
                                        .type(JsonFieldType.NUMBER)
                                        .description("입학설명회 정원"),
                                fieldWithPath("place")
                                        .type(JsonFieldType.STRING)
                                        .description("입학설명회 장소"),
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("<<fair-type,입학설명회 유형>>"),
                                fieldWithPath("applicationStartDate")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("입학설명회 신청 시작일 (yyyy-MM-dd)"),
                                fieldWithPath("applicationEndDate")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("입학설명회 신청 종료일 (yyyy-MM-dd)")
                        )
                ));

        verify(createAdmissionFairUseCase, times(1)).execute(any(CreateFairRequest.class));
    }

    @Test
    void 입학설명회를_상세히_불러온다() throws Exception {
        Long fairId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFairDetailUseCase.execute(fairId)).willReturn(FairFixture.createFairDetailResponse());

        mockMvc.perform(get("/admin/fairs/{fair-id}", fairId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("fair-id")
                                        .description("입학설명회 id")
                        )
                ));

        verify(queryFairDetailUseCase, times(1)).execute(fairId);
    }

    @Test
    void 입학설명회를_상세히_불러올_때_해당_입학설명회가_없으면_에러가_발생한다() throws Exception {
        Long fairId = -1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FairNotFoundException()).given(queryFairDetailUseCase).execute(fairId);

        mockMvc.perform(get("/admin/fairs/{fair-id}", fairId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(queryFairDetailUseCase, times(1)).execute(fairId);
    }

    @Test
    void 입학설명회_신청자_명단을_엑셀로_다운받는다() throws Exception {
        Long fairId = 1L;
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "입학설명회참가자명단",
                "입학설명회참가자명단.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportAttendeeListUseCase.execute(fairId)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/admin/fairs/{fair-id}/export", fairId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("fair-id")
                                        .description("입학설명회 id")
                        )
                ));

        verify(exportAttendeeListUseCase, times(1)).execute(fairId);
    }

    @Test
    void 입학설명회_신청자_명단을_엑셀로_다운받을_때_입학설명회가_없으면_에러가_발생한다() throws Exception {
        Long fairId = -1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FairNotFoundException()).given(exportAttendeeListUseCase).execute(fairId);

        mockMvc.perform(get("/admin/fairs/{fair-id}/export", fairId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader()))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(exportAttendeeListUseCase, times(1)).execute(fairId);
    }

}
