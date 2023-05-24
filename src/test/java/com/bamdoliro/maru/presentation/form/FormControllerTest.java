package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.ApplicantRequest;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.security.auth.AuthDetails;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FormControllerTest extends RestDocsTestSupport {

    @Test
    void 원서를_접수한다() throws Exception {
        User user = UserFixture.createUser();
        FormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        willDoNothing().given(submitFormUseCase).execute(request);
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

        mockMvc.perform(post("/form")
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
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("원서 유형"),
                                fieldWithPath("applicant.name")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 이름"),
                                fieldWithPath("applicant.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 전화번호"),
                                fieldWithPath("applicant.birthDay")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 생년월일"),
                                fieldWithPath("applicant.gender")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 성별"),
                                fieldWithPath("parent.name")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 이름"),
                                fieldWithPath("parent.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 전화번호"),
                                fieldWithPath("parent.zoneCode")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 주소지 우편번호"),
                                fieldWithPath("parent.address")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 주소지"),
                                fieldWithPath("parent.detailAddress")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 상세주소"),
                                fieldWithPath("education.graduationType")
                                        .type(JsonFieldType.STRING)
                                        .description("졸업 유형"),
                                fieldWithPath("education.graduationYear")
                                        .type(JsonFieldType.STRING)
                                        .description("졸업 연도"),
                                fieldWithPath("grade.subjectList[].grade")
                                        .type(JsonFieldType.NUMBER)
                                        .description("학년 (검정고시는 1로 통일)"),
                                fieldWithPath("grade.subjectList[].semester")
                                        .type(JsonFieldType.NUMBER)
                                        .description("학기 (검정고시는 1로 통일)"),
                                fieldWithPath("grade.subjectList[].subjectName")
                                        .type(JsonFieldType.STRING)
                                        .description("과목명"),
                                fieldWithPath("grade.subjectList[].achievementLevel")
                                        .type(JsonFieldType.STRING)
                                        .description("성취도 (A-E)"),
                                fieldWithPath("grade.certificateList[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("자격증 리스트"),
                                fieldWithPath("grade.attendance1.absenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("1학년 미인정 결석 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance1.latenessCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("1학년 미인정 지각 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance1.earlyLeaveCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("1학년 미인정 조퇴 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance1.classAbsenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("1학년 미인정 결과 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.absenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("2학년 미인정 결석 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.latenessCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("2학년 미인정 지각 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.earlyLeaveCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("2학년 미인정 조퇴 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.classAbsenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("2학년 미인정 결과 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.absenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("3학년 미인정 결석 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.latenessCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("3학년 미인정 지각 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.earlyLeaveCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("3학년 미인정 조퇴 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.classAbsenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("3학년 미인정 결과 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.volunteerTime1")
                                        .type(JsonFieldType.NUMBER)
                                        .description("1학년 봉사시간 (봉사 성적이 없는 경우 null)"),
                                fieldWithPath("grade.volunteerTime2")
                                        .type(JsonFieldType.NUMBER)
                                        .description("2학년 봉사시간 (봉사 성적이 없는 경우 null)"),
                                fieldWithPath("grade.volunteerTime3")
                                        .type(JsonFieldType.NUMBER)
                                        .description("3학년 봉사시간 (봉사 성적이 없는 경우 null)"),
                                fieldWithPath("document.coverLetter")
                                        .type(JsonFieldType.STRING)
                                        .description("1600자 이내의 자기소개서"),
                                fieldWithPath("document.statementOfPurpose")
                                        .type(JsonFieldType.STRING)
                                        .description("1600자 이내의 학업계획서")
                        )
                ));
    }

    @Test
    void 중졸_껌정고시_합격자가_원서를_접수한다() throws Exception {
        User user = UserFixture.createUser();
        FormRequest request = FormFixture.createQualificationExaminationFormRequest(FormType.MEISTER_TALENT);
        willDoNothing().given(submitFormUseCase).execute(request);
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

        mockMvc.perform(post("/form")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document());
    }

    @Test
    void 원서를_접수할_때_이미_접수한_원서가_있으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));
        doThrow(new FormAlreadySubmittedException()).when(submitFormUseCase).execute(any(FormRequest.class));

        mockMvc.perform(post("/form")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isConflict())

                .andDo(restDocs.document());
    }

    @Test
    void 원서를_접수할_때_잘못된_형식의_요청을_보내면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FormRequest request = new FormRequest();
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

        mockMvc.perform(post("/form")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(submitFormUseCase, never()).execute(any(FormRequest.class));
    }
}