package com.bamdoliro.maru.presentation.analysis;


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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalysisControllerTest extends RestDocsTestSupport {

    @Test
    void 전형별_지원자수를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryNumberOfApplicantsUseCase.execute(any(String.class))).willReturn(AnalysisFixture.createNumberOfApplicantsResponseList());

        mockMvc.perform(get("/analysis/number-of-applicants")
                        .param("type", "CURRENT")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("type")
                                        .description("CURRENT(지원전형) / ORIGINAL(최종 전형)")
                        )
                ));
    }

    @Test
    void _1차_합격자들의_성적_분포를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryGradeDistributionUseCase.execute(any(GradeDistributionRequest.class))).willReturn(AnalysisFixture.createGradeDistributionResponseList());

        mockMvc.perform(get("/analysis/grade-distribution")
                        .param("statusList", "FIRST_PASSED", "FAILED", "PASSED")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)")
                        )
                ));
    }

    @Test
    void _2차_전형자들의_성적_분포를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryGradeDistributionUseCase.execute(any(GradeDistributionRequest.class))).willReturn(AnalysisFixture.createGradeDistributionResponseList());

        mockMvc.perform(get("/analysis/grade-distribution")
                        .param("statusList", "FAILED", "PASSED")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)")
                        )
                ));
    }

    @Test
    void 최종_합격자들의_성적_분포를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryGradeDistributionUseCase.execute(any(GradeDistributionRequest.class))).willReturn(AnalysisFixture.createGradeDistributionResponseList());

        mockMvc.perform(get("/analysis/grade-distribution")
                        .param("statusList", "PASSED")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)")
                        )
                ));
    }

    @Test
    void 전형별_성비를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("statusList", List.of("FIRST_PASSED", "FAILED", "PASSED"));
        multiValueMap.add("mainCategory", "REGULAR");
        multiValueMap.add("type", "CURRENT");
        given(queryGenderRatioUseCase.execute(any(GenderRatioRequest.class))).willReturn(AnalysisFixture.createGenderRatioResponse(
                FormType.Category.valueOf(Objects.requireNonNull(multiValueMap.get("mainCategory")).get(0))
        ));

        mockMvc.perform(get("/analysis/gender-ratio")
                        .params(multiValueMap)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                parameterWithName("mainCategory")
                                        .description("메인 카테고리(FormType.Category 참고)"),
                                parameterWithName("type")
                                        .description("CURRENT(지원전형) / ORIGINAL(최종 전형)")
                        )
                ));
    }

    @Test
    void 부산_특정구_출신_지원자들의_출신학교_통계를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("statusList", List.of("FIRST_PASSED", "FAILED", "PASSED"));
        multiValueMap.add("isBusan", "true");
        multiValueMap.add("gu", "사상구");
        given(querySchoolStatusUseCase.execute(any(SchoolStatusRequest.class))).willReturn(AnalysisFixture.createSchoolStatusResponse(Objects.requireNonNull(multiValueMap.get("isBusan")), multiValueMap.get("gu")));

        mockMvc.perform(get("/analysis/school-status")
                        .params(multiValueMap)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                parameterWithName("isBusan")
                                        .description("부산 지역 학교 검색 여부(true면 부산, false면 부산 외 다른 모든 타지역)"),
                                parameterWithName("gu")
                                        .description("구(isBusan이 true이고, null인 경우 부산지역 전체 조회)")
                        )
                ));
    }

    @Test
    void 부산_출신_지원자들의_출신학교_통계를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("statusList", List.of("FIRST_PASSED", "FAILED", "PASSED"));
        multiValueMap.add("isBusan", "true");
        multiValueMap.add("gu", null);
        given(querySchoolStatusUseCase.execute(any(SchoolStatusRequest.class))).willReturn(AnalysisFixture.createSchoolStatusResponse(Objects.requireNonNull(multiValueMap.get("isBusan")), multiValueMap.get("gu")));

        mockMvc.perform(get("/analysis/school-status")
                        .params(multiValueMap)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                parameterWithName("isBusan")
                                        .description("부산 지역 학교 검색 여부(true면 부산, false면 부산 외 다른 모든 타지역)"),
                                parameterWithName("gu")
                                        .description("구(isBusan이 true이고, null인 경우 부산지역 전체 조회)")
                        )
                ));
    }

    @Test
    void 타지역_출신_지원자들의_출신학교_통계를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("statusList", List.of("FIRST_PASSED", "FAILED", "PASSED"));
        multiValueMap.add("isBusan", "false");
        multiValueMap.add("gu", null);
        given(querySchoolStatusUseCase.execute(any(SchoolStatusRequest.class))).willReturn(AnalysisFixture.createSchoolStatusResponse(Objects.requireNonNull(multiValueMap.get("isBusan")), multiValueMap.get("gu")));

        mockMvc.perform(get("/analysis/school-status")
                        .params(multiValueMap)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("statusList")
                                        .description("조회할 원서 상태 목록(전체 조회면, RECEIVED, FIRST_PASSED, FAILED, PASSED, 1차 합격자면 FIRST_PASSED, FAILED, PASSED, 2차 전형자면 FAILED, PASSED, 최종 합격자면 PASSED)"),
                                parameterWithName("isBusan")
                                        .description("부산 지역 학교 검색 여부(true면 부산, false면 부산 외 다른 모든 타지역)"),
                                parameterWithName("gu")
                                        .description("구(isBusan이 true이고, null인 경우 부산지역 전체 조회)")
                        )
                ));
    }
}