package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.domain.form.exception.DraftFormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.DraftFormFixture;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DraftFormControllerTest extends RestDocsTestSupport {

    @Test
    void 원서를_임시저장한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(draftFormUseCase).execute(any(User.class), any(SubmitFormRequest.class));

        mockMvc.perform(post("/form/draft")
                .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(DraftFormFixture.createDraftFormRequest())))

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("<<form-type,원서 유형>>"),
                                fieldWithPath("applicant.name")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("지원자 이름"),
                                fieldWithPath("applicant.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("지원자 전화번호"),
                                fieldWithPath("applicant.birthday")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("지원자 생년월일 (yyyy-MM-dd)"),
                                fieldWithPath("applicant.gender")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("<<gender,지원자 성별>>"),
                                fieldWithPath("parent.name")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("보호자 이름"),
                                fieldWithPath("parent.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("보호자 전화번호"),
                                fieldWithPath("parent.relation")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("보호자 관계"),
                                fieldWithPath("parent.zoneCode")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("보호자 주소지 우편번호"),
                                fieldWithPath("parent.address")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("보호자 주소지"),
                                fieldWithPath("parent.detailAddress")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("보호자 상세주소"),
                                fieldWithPath("education.graduationType")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("<<graduation-type,졸업 유형>>"),
                                fieldWithPath("education.graduationYear")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("졸업 연도"),
                                fieldWithPath("education.schoolName")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("출신 학교 이름 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.schoolLocation")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("출신 학교 지역 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.schoolCode")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("출신 학교 코드 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.teacherName")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("작성 교사 (없는 경우 null)"),
                                fieldWithPath("education.teacherPhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("작성 교사 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.teacherMobilePhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("작성 교사 휴대전화번호 (없는 경우 null)"),
                                fieldWithPath("grade.subjectList[].subjectName")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("과목명"),
                                fieldWithPath("grade.subjectList[].achievementLevel21")
                                        .type(JsonFieldType.STRING)
                                        .description("<<achievement-level,2학년 1학기 성취도 (성적이 없는 경우 null)>>")
                                        .optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel22")
                                        .type(JsonFieldType.STRING)
                                        .description("<<achievement-level,2학년 2학기 성취도 (성적이 없는 경우 null)>>")
                                        .optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel31")
                                        .type(JsonFieldType.STRING)
                                        .description("<<achievement-level,3학년 1학기 성취도 (성적이 없는 경우 null)>>")
                                        .optional(),
                                fieldWithPath("grade.certificateList[]")
                                        .type(JsonFieldType.ARRAY)
                                        .optional()
                                        .description("<<certificate,자격증 리스트>>"),
                                fieldWithPath("grade.attendance1.absenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("1학년 미인정 결석 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance1.latenessCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("1학년 미인정 지각 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance1.earlyLeaveCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("1학년 미인정 조퇴 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance1.classAbsenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("1학년 미인정 결과 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.absenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("2학년 미인정 결석 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.latenessCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("2학년 미인정 지각 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.earlyLeaveCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("2학년 미인정 조퇴 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance2.classAbsenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("2학년 미인정 결과 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.absenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("3학년 미인정 결석 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.latenessCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("3학년 미인정 지각 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.earlyLeaveCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("3학년 미인정 조퇴 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.attendance3.classAbsenceCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("3학년 미인정 결과 횟수 (출결 성적이 없는 경우 null)"),
                                fieldWithPath("grade.volunteerTime1")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("1학년 봉사시간 (봉사 성적이 없는 경우 null)"),
                                fieldWithPath("grade.volunteerTime2")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("2학년 봉사시간 (봉사 성적이 없는 경우 null)"),
                                fieldWithPath("grade.volunteerTime3")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("3학년 봉사시간 (봉사 성적이 없는 경우 null)"),
                                fieldWithPath("document.coverLetter")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("1600자 이내의 자기소개서"),
                                fieldWithPath("document.statementOfPurpose")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("1600자 이내의 학업계획서"),
                                fieldWithPath("education")
                                        .ignored(),
                                fieldWithPath("grade")
                                        .ignored(),
                                fieldWithPath("document")
                                        .ignored()
                        )
                ));

        verify(draftFormUseCase, times(1)).execute(any(User.class), any(SubmitFormRequest.class));
    }

    @Test
    void 임시저장된_원서를_조회한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryDraftFormUseCase.execute(user)).willReturn(DraftFormFixture.createDraftFormRequest());

        mockMvc.perform(get("/form/draft")
                .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(queryDraftFormUseCase, times(1)).execute(user);
    }

    @Test
    void 임시저장된_원서를_조회할_때_임시저장된_원서가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new DraftFormNotFoundException()).when(queryDraftFormUseCase).execute(user);

        mockMvc.perform(get("/form/draft")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(queryDraftFormUseCase, times(1)).execute(user);
    }
}
