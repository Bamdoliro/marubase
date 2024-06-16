package com.bamdoliro.maru.presentation.analysis;


import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.analysis.dto.request.GenderRatioRequest;
import com.bamdoliro.maru.presentation.analysis.dto.request.GradeDistributionRequest;
import com.bamdoliro.maru.presentation.analysis.dto.request.SchoolStatusRequest;
import com.bamdoliro.maru.shared.fixture.AnalysisFixture;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalysisControllerTest extends RestDocsTestSupport {

    @Test
    void 전형별_지원자수를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryNumberOfApplicantsUseCase.execute()).willReturn(AnalysisFixture.createNumberOfApplicantsResponse());

        mockMvc.perform(get("/analysis/number-of-applicants")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        )
                ));
    }

    @Test
    void _1차_합격자들의_성적_분포를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(
                FormStatus.FIRST_PASSED,
                FormStatus.FAILED,
                FormStatus.PASSED
        ));
        System.out.println(AnalysisFixture.createGradeDistributionResponse());
        given(queryGradeDistributionUseCase.execute(any(GradeDistributionRequest.class))).willReturn(AnalysisFixture.createGradeDistributionResponse());

        mockMvc.perform(get("/analysis/grade-distribution")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)")
                        )
                ));
    }

    @Test
    void _2차_전형자들의_성적_분포를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(
                FormStatus.FAILED,
                FormStatus.PASSED
        ));
        given(queryGradeDistributionUseCase.execute(any(GradeDistributionRequest.class))).willReturn(AnalysisFixture.createGradeDistributionResponse());

        mockMvc.perform(get("/analysis/grade-distribution")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)")
                        )
                ));
    }

    @Test
    void 최종_합격자들의_성적_분포를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(
                FormStatus.PASSED
        ));
        given(queryGradeDistributionUseCase.execute(any(GradeDistributionRequest.class))).willReturn(AnalysisFixture.createGradeDistributionResponse());

        mockMvc.perform(get("/analysis/grade-distribution")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)")
                        )
                ));
    }

    @Test
    void 전형별_성비를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        GenderRatioRequest request = new GenderRatioRequest(List.of(
                FormStatus.FIRST_PASSED,
                FormStatus.FAILED,
                FormStatus.PASSED
        ),
                FormType.Category.REGULAR);
        given(queryGenderRatioUseCase.execute(any(GenderRatioRequest.class))).willReturn(AnalysisFixture.createGenderRatioResponse(FormType.Category.REGULAR));

        mockMvc.perform(get("/analysis/gender-ratio")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                fieldWithPath("mainCategory").type(JsonFieldType.STRING).description("메인 카테고리(FormType.Category 참고)")
                        )
                ));
    }

    @Test
    void 부산_특정구_출신_지원자들의_출신학교_통계를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        SchoolStatusRequest request = new SchoolStatusRequest(List.of(
                FormStatus.FIRST_PASSED,
                FormStatus.FAILED,
                FormStatus.PASSED
        ),
                true,
                "사상구");
        given(querySchoolStatusUseCase.execute(any(SchoolStatusRequest.class))).willReturn(AnalysisFixture.createSchoolStatusResponse(request.getIsBusan(), request.getGu()));

        mockMvc.perform(get("/analysis/school-status")
                .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                fieldWithPath("isBusan").type(JsonFieldType.BOOLEAN).description("부산 지역 학교 검색 여부(true면 부산, false면 부산 외 다른 모든 타지역)"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).optional().description("구(isBusan이 true이고, null인 경우 부산지역 전체 조회)")
                        )
                ));
    }

    @Test
    void 부산_출신_지원자들의_출신학교_통계를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        SchoolStatusRequest request = new SchoolStatusRequest(List.of(
                FormStatus.FIRST_PASSED,
                FormStatus.FAILED,
                FormStatus.PASSED
        ),
                true,
                null);
        given(querySchoolStatusUseCase.execute(any(SchoolStatusRequest.class))).willReturn(AnalysisFixture.createSchoolStatusResponse(request.getIsBusan(), request.getGu()));

        mockMvc.perform(get("/analysis/school-status")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                fieldWithPath("isBusan").type(JsonFieldType.BOOLEAN).description("부산 지역 학교 검색 여부(true면 부산, false면 부산 외 다른 모든 타지역)"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).optional().description("구(isBusan이 true이고, null인 경우 부산지역 전체 조회)")
                        )
                ));
    }

    @Test
    void 타지역_출신_지원자들의_출신학교_통계를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        SchoolStatusRequest request = new SchoolStatusRequest(List.of(
                FormStatus.FIRST_PASSED,
                FormStatus.FAILED,
                FormStatus.PASSED
        ),
                false,
                null);
        given(querySchoolStatusUseCase.execute(any(SchoolStatusRequest.class))).willReturn(AnalysisFixture.createSchoolStatusResponse(request.getIsBusan(), request.getGu()));

        mockMvc.perform(get("/analysis/school-status")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("statusList").type(JsonFieldType.ARRAY).description("조회할 원서 상태 목록(1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                fieldWithPath("isBusan").type(JsonFieldType.BOOLEAN).description("부산 지역 학교 검색 여부(true면 부산, false면 부산 외 다른 모든 타지역)"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).optional().description("구(isBusan이 true이고, null인 경우 부산지역 전체 조회)")
                        )
                ));
    }
}