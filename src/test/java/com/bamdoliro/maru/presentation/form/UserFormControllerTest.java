package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.exception.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.exception.FailedToExportPdfException;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.MediaTypeMismatchException;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormResultResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserFormControllerTest extends RestDocsTestSupport {

    @Test
    void 원서를_제출한다() throws Exception {
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(submitFormUseCase).execute(user, request);

        mockMvc.perform(post("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        ),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING).description("<<form-type,원서 유형>>"),
                                fieldWithPath("applicant.name").type(JsonFieldType.STRING).description("지원자 이름"),
                                fieldWithPath("applicant.phoneNumber").type(JsonFieldType.STRING).description("지원자 전화번호"),
                                fieldWithPath("applicant.birthday").type(JsonFieldType.STRING).description("지원자 생년월일 (yyyy-MM-dd)"),
                                fieldWithPath("applicant.gender").type(JsonFieldType.STRING).description("<<gender,지원자 성별>>"),
                                fieldWithPath("parent.name").type(JsonFieldType.STRING).description("보호자 이름"),
                                fieldWithPath("parent.phoneNumber").type(JsonFieldType.STRING).description("보호자 전화번호"),
                                fieldWithPath("parent.relation").type(JsonFieldType.STRING).description("보호자 관계"),
                                fieldWithPath("parent.zoneCode").type(JsonFieldType.STRING).description("보호자 주소지 우편번호"),
                                fieldWithPath("parent.address").type(JsonFieldType.STRING).description("보호자 주소지"),
                                fieldWithPath("parent.detailAddress").type(JsonFieldType.STRING).description("보호자 상세주소"),
                                fieldWithPath("education.graduationType").type(JsonFieldType.STRING).description("<<graduation-type,졸업 유형>>"),
                                fieldWithPath("education.graduationYear").type(JsonFieldType.STRING).description("졸업 연도, 합격 연도"),
                                fieldWithPath("education.schoolName").type(JsonFieldType.STRING).description("출신 학교 이름 (없는 경우 null)"),
                                fieldWithPath("education.schoolLocation").type(JsonFieldType.STRING).description("출신 학교 지역 (없는 경우 null)"),
                                fieldWithPath("education.schoolAddress").type(JsonFieldType.STRING).description("출신 학교 주소지 (없는 경우 null)"),
                                fieldWithPath("education.schoolCode").type(JsonFieldType.STRING).description("출신 학교 코드 (없는 경우 null)"),
                                fieldWithPath("education.teacherName").type(JsonFieldType.STRING).description("출신 학교 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.schoolPhoneNumber").type(JsonFieldType.STRING).description("작성 교사 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.teacherMobilePhoneNumber").type(JsonFieldType.STRING).description("작성 교사 휴대전화번호 (없는 경우 null)"),
                                fieldWithPath("grade.subjectList[].subjectName").type(JsonFieldType.STRING).description("과목명"),
                                fieldWithPath("grade.subjectList[].achievementLevel11").type(JsonFieldType.STRING).description("<<achievement-level,1학년 1학기 성취도>> (정보 과목에만 존재)").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel12").type(JsonFieldType.STRING).description("<<achievement-level,1학년 2학기 성취도>> (정보 과목에만 존재)").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel21").type(JsonFieldType.STRING).description("<<achievement-level,2학년 1학기 성취도>>").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel22").type(JsonFieldType.STRING).description("<<achievement-level,2학년 2학기 성취도>>").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel31").type(JsonFieldType.STRING).description("<<achievement-level,3학년 1학기 성취도>>").optional(),
                                fieldWithPath("grade.subjectList[].score").type(JsonFieldType.NUMBER).description("검정고시 점수 (검정고시가 아닐시 null)").optional(),
                                fieldWithPath("grade.certificateList[]").type(JsonFieldType.ARRAY).description("<<certificate,자격증 리스트>>"),
                                fieldWithPath("grade.attendance1.absenceCount").type(JsonFieldType.NUMBER).description("1학년 미인정 결석"),
                                fieldWithPath("grade.attendance1.latenessCount").type(JsonFieldType.NUMBER).description("1학년 미인정 지각"),
                                fieldWithPath("grade.attendance1.earlyLeaveCount").type(JsonFieldType.NUMBER).description("1학년 미인정 조퇴"),
                                fieldWithPath("grade.attendance1.classAbsenceCount").type(JsonFieldType.NUMBER).description("1학년 미인정 결과"),
                                fieldWithPath("grade.attendance2.absenceCount").type(JsonFieldType.NUMBER).description("2학년 미인정 결석"),
                                fieldWithPath("grade.attendance2.latenessCount").type(JsonFieldType.NUMBER).description("2학년 미인정 지각"),
                                fieldWithPath("grade.attendance2.earlyLeaveCount").type(JsonFieldType.NUMBER).description("2학년 미인정 조퇴"),
                                fieldWithPath("grade.attendance2.classAbsenceCount").type(JsonFieldType.NUMBER).description("2학년 미인정 결과"),
                                fieldWithPath("grade.attendance3.absenceCount").type(JsonFieldType.NUMBER).description("3학년 미인정 결석"),
                                fieldWithPath("grade.attendance3.latenessCount").type(JsonFieldType.NUMBER).description("3학년 미인정 지각"),
                                fieldWithPath("grade.attendance3.earlyLeaveCount").type(JsonFieldType.NUMBER).description("3학년 미인정 조퇴"),
                                fieldWithPath("grade.attendance3.classAbsenceCount").type(JsonFieldType.NUMBER).description("3학년 미인정 결과"),
                                fieldWithPath("grade.volunteerTime1").type(JsonFieldType.NUMBER).description("1학년 봉사시간"),
                                fieldWithPath("grade.volunteerTime2").type(JsonFieldType.NUMBER).description("2학년 봉사시간"),
                                fieldWithPath("grade.volunteerTime3").type(JsonFieldType.NUMBER).description("3학년 봉사시간"),
                                fieldWithPath("grade.mentoringProgram").type(JsonFieldType.BOOLEAN).description("멘토링 프로그램 참여 여부"),
                                fieldWithPath("document.coverLetter").type(JsonFieldType.STRING).description("1600자 이내의 자기소개서"),
                                fieldWithPath("document.statementOfPurpose").type(JsonFieldType.STRING).description("1600자 이내의 학업계획서")
                        )
                ));
    }

    @Test
    void 중졸_검정고시_합격자가_원서를_제출한다() throws Exception {
        SubmitFormRequest request = FormFixture.createQualificationExaminationFormRequest(FormType.MEISTER_TALENT);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(submitFormUseCase).execute(user, request);

        mockMvc.perform(post("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isCreated())
                .andDo(restDocs.document());
    }

    @Test
    void 원서를_제출할_때_원서_접수_기간이_아니면_에러가_발생한다() throws Exception {
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new OutOfApplicationFormPeriodException()).when(submitFormUseCase).execute(any(User.class), any(SubmitFormRequest.class));

        mockMvc.perform(post("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(submitFormUseCase, times(1)).execute(any(User.class), any(SubmitFormRequest.class));
    }

    @Test
    void 원서를_제출할_때_이미_제출한_원서가_있으면_에러가_발생한다() throws Exception {
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormAlreadySubmittedException()).when(submitFormUseCase).execute(any(User.class), any(SubmitFormRequest.class));

        mockMvc.perform(post("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());
    }

    @Test
    void 원서를_제출할_때_잘못된_형식의_요청을_보내면_에러가_발생한다() throws Exception {
        SubmitFormRequest request = new SubmitFormRequest();
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(post("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(submitFormUseCase, never()).execute(any(User.class), any(SubmitFormRequest.class));
    }

    @Test
    void 원서를_최종_제출한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(submitFinalFormUseCase).execute(any(User.class));

        mockMvc.perform(patch("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(submitFinalFormUseCase, times(1)).execute(any(User.class));
    }

    @Test
    void 원서를_최종_제출할_때_원서_접수_기간이_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new OutOfApplicationFormPeriodException()).when(submitFinalFormUseCase).execute(any(User.class));

        mockMvc.perform(patch("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(submitFinalFormUseCase, times(1)).execute(any(User.class));
    }

    @Test
    void 원서를_최종_제출할_때_이미_제출한_원서라면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormAlreadySubmittedException()).when(submitFinalFormUseCase).execute(any(User.class));

        mockMvc.perform(patch("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(submitFinalFormUseCase, times(1)).execute(any(User.class));
    }

    @Test
    void 원서_상태를_조회한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormStatusUseCase.execute(user)).willReturn(FormFixture.createFormSimpleResponse(FormStatus.APPROVED));

        mockMvc.perform(get("/forms/status")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(queryFormStatusUseCase, times(1)).execute(user);
    }

    @Test
    void 원서_상태를_조회할_때_원서가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormStatusUseCase.execute(user)).willThrow(new FormNotFoundException());

        mockMvc.perform(get("/forms/status")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());
    }

    @Test
    void 원서를_수정한다() throws Exception {
        UpdateFormRequest request = FormFixture.createUpdateFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(updateFormUseCase).execute(user, request);

        mockMvc.perform(put("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        ),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING).description("<<form-type,원서 유형>>"),
                                fieldWithPath("applicant.name").type(JsonFieldType.STRING).description("지원자 이름"),
                                fieldWithPath("applicant.phoneNumber").type(JsonFieldType.STRING).description("지원자 전화번호"),
                                fieldWithPath("applicant.birthday").type(JsonFieldType.STRING).description("지원자 생년월일"),
                                fieldWithPath("applicant.gender").type(JsonFieldType.STRING).description("<<gender,지원자 성별>>"),
                                fieldWithPath("parent.name").type(JsonFieldType.STRING).description("보호자 이름"),
                                fieldWithPath("parent.phoneNumber").type(JsonFieldType.STRING).description("보호자 전화번호"),
                                fieldWithPath("parent.relation").type(JsonFieldType.STRING).description("보호자 관계"),
                                fieldWithPath("parent.zoneCode").type(JsonFieldType.STRING).description("보호자 주소지 우편번호"),
                                fieldWithPath("parent.address").type(JsonFieldType.STRING).description("보호자 주소지"),
                                fieldWithPath("parent.detailAddress").type(JsonFieldType.STRING).description("보호자 상세주소"),
                                fieldWithPath("education.graduationType").type(JsonFieldType.STRING).description("<<graduation-type,졸업 유형>>"),
                                fieldWithPath("education.graduationYear").type(JsonFieldType.STRING).description("졸업 연도"),
                                fieldWithPath("education.schoolName").type(JsonFieldType.STRING).description("출신 학교 이름"),
                                fieldWithPath("education.schoolLocation").type(JsonFieldType.STRING).description("출신 학교 지역"),
                                fieldWithPath("education.schoolAddress").type(JsonFieldType.STRING).description("출신 학교 주소지"),
                                fieldWithPath("education.schoolCode").type(JsonFieldType.STRING).description("출신 학교 코드"),
                                fieldWithPath("education.teacherName").type(JsonFieldType.STRING).description("출신 학교 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.schoolPhoneNumber").type(JsonFieldType.STRING).description("작성 교사 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.teacherMobilePhoneNumber").type(JsonFieldType.STRING).description("작성 교사 휴대전화번호 (없는 경우 null)"),
                                fieldWithPath("grade.subjectList[].subjectName").type(JsonFieldType.STRING).description("과목명"),
                                fieldWithPath("grade.subjectList[].achievementLevel11").type(JsonFieldType.STRING).description("<<achievement-level,1학년 1학기 성취도>> (정보 과목에만 존재)").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel12").type(JsonFieldType.STRING).description("<<achievement-level,1학년 2학기 성취도>> (정보 과목에만 존재)").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel21").type(JsonFieldType.STRING).description("<<achievement-level,2학년 1학기 성취도>>").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel22").type(JsonFieldType.STRING).description("<<achievement-level,2학년 2학기 성취도>>").optional(),
                                fieldWithPath("grade.subjectList[].achievementLevel31").type(JsonFieldType.STRING).description("<<achievement-level,3학년 1학기 성취도>>").optional(),
                                fieldWithPath("grade.subjectList[].score").type(JsonFieldType.NUMBER).description("검정고시 점수 (검정고시가 아닐시 null)").optional(),
                                fieldWithPath("grade.certificateList[]").type(JsonFieldType.ARRAY).description("<<certificate,자격증 리스트>>"),
                                fieldWithPath("grade.attendance1.absenceCount").type(JsonFieldType.NUMBER).description("1학년 미인정 결석"),
                                fieldWithPath("grade.attendance1.latenessCount").type(JsonFieldType.NUMBER).description("1학년 미인정 지각"),
                                fieldWithPath("grade.attendance1.earlyLeaveCount").type(JsonFieldType.NUMBER).description("1학년 미인정 조퇴"),
                                fieldWithPath("grade.attendance1.classAbsenceCount").type(JsonFieldType.NUMBER).description("1학년 미인정 결과"),
                                fieldWithPath("grade.attendance2.absenceCount").type(JsonFieldType.NUMBER).description("2학년 미인정 결석"),
                                fieldWithPath("grade.attendance2.latenessCount").type(JsonFieldType.NUMBER).description("2학년 미인정 지각"),
                                fieldWithPath("grade.attendance2.earlyLeaveCount").type(JsonFieldType.NUMBER).description("2학년 미인정 조퇴"),
                                fieldWithPath("grade.attendance2.classAbsenceCount").type(JsonFieldType.NUMBER).description("2학년 미인정 결과"),
                                fieldWithPath("grade.attendance3.absenceCount").type(JsonFieldType.NUMBER).description("3학년 미인정 결석"),
                                fieldWithPath("grade.attendance3.latenessCount").type(JsonFieldType.NUMBER).description("3학년 미인정 지각"),
                                fieldWithPath("grade.attendance3.earlyLeaveCount").type(JsonFieldType.NUMBER).description("3학년 미인정 조퇴"),
                                fieldWithPath("grade.attendance3.classAbsenceCount").type(JsonFieldType.NUMBER).description("3학년 미인정 결과"),
                                fieldWithPath("grade.volunteerTime1").type(JsonFieldType.NUMBER).description("1학년 봉사시간"),
                                fieldWithPath("grade.volunteerTime2").type(JsonFieldType.NUMBER).description("2학년 봉사시간"),
                                fieldWithPath("grade.volunteerTime3").type(JsonFieldType.NUMBER).description("3학년 봉사시간"),
                                fieldWithPath("grade.mentoringProgram").type(JsonFieldType.BOOLEAN).description("멘토링 프로그램 참여 여부"),
                                fieldWithPath("document.coverLetter").type(JsonFieldType.STRING).description("1600자 이내의 자기소개서"),
                                fieldWithPath("document.statementOfPurpose").type(JsonFieldType.STRING).description("1600자 이내의 학업계획서")
                        )
                ));

        verify(updateFormUseCase, times(1)).execute(any(User.class), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_원서_접수_기간이_아니면_에러가_발생한다() throws Exception {
        UpdateFormRequest request = FormFixture.createUpdateFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new OutOfApplicationFormPeriodException()).when(updateFormUseCase).execute(any(User.class), any(UpdateFormRequest.class));

        mockMvc.perform(put("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_원서가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(updateFormUseCase).execute(any(User.class), any(UpdateFormRequest.class));

        mockMvc.perform(put("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createUpdateFormRequest(FormType.REGULAR)))
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_본인의_원서가_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new AuthorityMismatchException()).when(updateFormUseCase).execute(any(User.class), any(UpdateFormRequest.class));

        mockMvc.perform(put("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createUpdateFormRequest(FormType.REGULAR)))
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_반려된_원서가_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new CannotUpdateNotRejectedFormException()).when(updateFormUseCase).execute(any(User.class), any(UpdateFormRequest.class));

        mockMvc.perform(put("/forms")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createUpdateFormRequest(FormType.REGULAR)))
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), any(UpdateFormRequest.class));
    }

    @Test
    void 증명_사진을_업로드한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("identification-picture.png", MediaType.IMAGE_PNG_VALUE, MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadIdentificationPictureUseCase.execute(any(User.class), any(FileMetadata.class))).willReturn(SharedFixture.createIdentificationPictureUrlResponse());

        mockMvc.perform(post("/forms/identification-picture")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        ),
                        requestFields(
                                fieldWithPath("fileName").description("파일 이름"),
                                fieldWithPath("mediaType").description("미디어 타입"),
                                fieldWithPath("fileSize").description("파일 용량")
                        )
                ));

        verify(uploadIdentificationPictureUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 증명_사진을_업로드할_때_원서_접수_기간이_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("identification-picture.png", MediaType.IMAGE_PNG_VALUE, MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new OutOfApplicationFormPeriodException()).when(uploadIdentificationPictureUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/identification-picture")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 증명_사진을_업로드할_때_반려_또는_미제출_상태가_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("identification-picture.png", MediaType.IMAGE_PNG_VALUE, MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFormStatusException()).when(uploadIdentificationPictureUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/identification-picture")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 증명_사진을_업로드할_때_파일이_비었으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("identification-picture.png", MediaType.IMAGE_PNG_VALUE, 0L);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new EmptyFileException()).when(uploadIdentificationPictureUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/identification-picture")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 증명_사진을_업로드할_때_파일이_용량_제한을_넘으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("identification-picture.png", MediaType.IMAGE_PNG_VALUE, 3 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FileSizeLimitExceededException(2)).when(uploadIdentificationPictureUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/identification-picture")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 증명_사진을_업로드할_때_콘텐츠_타입이_다르다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("identification-picture.bat", MediaType.TEXT_PLAIN_VALUE, MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new MediaTypeMismatchException()).when(uploadIdentificationPictureUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/identification-picture")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isUnsupportedMediaType())
                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서_서류를_업로드한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("form.pdf", MediaType.APPLICATION_PDF_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadFormUseCase.execute(any(User.class), any(FileMetadata.class))).willReturn(SharedFixture.createFormUrlResponse());

        mockMvc.perform(post("/forms/form-document")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        ),
                        requestFields(
                                fieldWithPath("fileName").description("파일 이름"),
                                fieldWithPath("mediaType").description("미디어 타입"),
                                fieldWithPath("fileSize").description("파일 용량")
                        )
                ));

        verify(uploadFormUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서_서류를_업로드할_때_제출_또는_반려_상태가_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("form.pdf", MediaType.APPLICATION_PDF_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFormStatusException()).when(uploadFormUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/form-document")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서_서류를_업로드할_때_원서_접수_기간이_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("form.pdf", MediaType.APPLICATION_PDF_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new OutOfApplicationFormPeriodException()).when(uploadFormUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/form-document")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서_서류를_업로드할_때_파일이_비었으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("form.pdf", MediaType.APPLICATION_PDF_VALUE, 0L);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new EmptyFileException()).when(uploadFormUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/form-document")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서_서류를_업로드할_때_파일이_용량_제한을_넘으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("form.pdf", MediaType.APPLICATION_PDF_VALUE, 21 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FileSizeLimitExceededException(20)).when(uploadFormUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/form-document")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서_서류를_업로드할_때_콘텐츠_타입이_다르다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("form.bat", MediaType.TEXT_PLAIN_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new MediaTypeMismatchException()).when(uploadFormUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/form-document")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isUnsupportedMediaType())
                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 최종합격자가_입학등록원_및_금연서약서_양식을_다운받는다() throws Exception {
        User user = UserFixture.createUser();
        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", MediaType.APPLICATION_PDF_VALUE, "<<file>>".getBytes());

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(downloadAdmissionAndPledgeFormatUseCase.execute(user)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_PDF)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(downloadAdmissionAndPledgeFormatUseCase, times(1)).execute(user);
    }

    @Test
    void 최종합격자가_아닌_사람이_입학등록원_및_금연서약서_양식을_다운받으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFormStatusException()).when(downloadAdmissionAndPledgeFormatUseCase).execute(user);

        mockMvc.perform(get("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(downloadAdmissionAndPledgeFormatUseCase, times(1)).execute(user);
    }

    @Test
    void 입학등록원_및_금연서약서를_다운받을_때_pdf변환에_실패했다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FailedToExportPdfException()).when(downloadAdmissionAndPledgeFormatUseCase).execute(user);

        mockMvc.perform(get("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andDo(restDocs.document());

        verify(downloadAdmissionAndPledgeFormatUseCase, times(1)).execute(user);
    }

    @Test
    void 입학등록원_및_금연서약서를_업로드한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("admission-and-pledge.pdf", MediaType.APPLICATION_PDF_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadAdmissionAndPledgeUseCase.execute(any(User.class), any(FileMetadata.class))).willReturn(SharedFixture.createAdmissionAndPledgeUrlResponse());

        mockMvc.perform(post("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        ),
                        requestFields(
                                fieldWithPath("fileName").description("파일 이름"),
                                fieldWithPath("mediaType").description("미디어 타입"),
                                fieldWithPath("fileSize").description("파일 용량")
                        )
                ));

        verify(uploadAdmissionAndPledgeUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 입학등록원_및_금연서약서를_업로드할_때_제출_기간이_아니면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("admission-and-pledge.pdf", MediaType.APPLICATION_PDF_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new OutOfAdmissionAndPledgePeriodException()).when(uploadAdmissionAndPledgeUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());

        verify(uploadAdmissionAndPledgeUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 최종합격자가_아닌_지원자가_입학등록원_및_금연서약서를_업로드하면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("admission-and-pledge.pdf", MediaType.APPLICATION_PDF_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFormStatusException()).when(uploadAdmissionAndPledgeUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(uploadAdmissionAndPledgeUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 입학등록원_및_금연서약서를_업로드할_때_파일이_용량_제한을_넘으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("admission-and-pledge.pdf", MediaType.APPLICATION_PDF_VALUE, 21 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FileSizeLimitExceededException(20)).when(uploadAdmissionAndPledgeUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(uploadAdmissionAndPledgeUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 입학등록원_및_금연서약서를_업로드할_때_콘텐츠_타입이_다르다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata("admission-and-pledge.bat", MediaType.TEXT_PLAIN_VALUE, 10 * MB);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new MediaTypeMismatchException()).when(uploadAdmissionAndPledgeUseCase).execute(any(User.class), any(FileMetadata.class));

        mockMvc.perform(post("/forms/admission-and-pledge")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadata))
                )
                .andExpect(status().isUnsupportedMediaType())
                .andDo(restDocs.document());

        verify(uploadAdmissionAndPledgeUseCase, times(1)).execute(any(User.class), any(FileMetadata.class));
    }

    @Test
    void 원서의_상태를_입학등록원_제출로_변경한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(enterFormUseCase).execute(user);

        mockMvc.perform(patch("/forms/enter")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));
    }

    @Test
    void 입학등록원을_제출했거나_최종합격한_지원자가_아닌_지원자가_원서의_상태를_변경하면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFormStatusException()).when(enterFormUseCase).execute(user);

        mockMvc.perform(patch("/forms/enter")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(enterFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서를_입학등록_상태로_변경할_때_원서가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(enterFormUseCase).execute(user);

        mockMvc.perform(patch("/forms/enter")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(enterFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서를_pdf로_다운받는다() throws Exception {
        User user = UserFixture.createUser();
        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", MediaType.APPLICATION_PDF_VALUE, "<<file>>".getBytes());

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportFormUseCase.execute(user)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/forms/export")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_PDF)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(exportFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서를_pdf로_다운받을_때_원서를_작성하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(exportFormUseCase).execute(user);

        mockMvc.perform(get("/forms/export")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(exportFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서를_pdf로_다운받을_때_원서를_이미_제출했다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormAlreadySubmittedException()).when(exportFormUseCase).execute(user);

        mockMvc.perform(get("/forms/export")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(exportFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서를_pdf로_다운받을_때_pdf변환에_실패했다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FailedToExportPdfException()).when(exportFormUseCase).execute(user);

        mockMvc.perform(get("/forms/export")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andDo(restDocs.document());

        verify(exportFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서의_1차_결과를_확인한다() throws Exception {
        Form form = FormFixture.createForm(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFirstFormResultUseCase.execute(user)).willReturn(new FormResultResponse(form, true));

        mockMvc.perform(get("/forms/result/first")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(queryFirstFormResultUseCase, times(1)).execute(user);
    }

    @Test
    void 원서의_1차_결과를_확인할_때_원서를_접수하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(queryFirstFormResultUseCase).execute(user);

        mockMvc.perform(get("/forms/result/first")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(queryFirstFormResultUseCase, times(1)).execute(user);
    }

    @Test
    void 원서의_최종_결과를_확인한다() throws Exception {
        Form form = FormFixture.createForm(FormType.MULTI_CHILDREN);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFinalFormResultUseCase.execute(user)).willReturn(new FormResultResponse(form, false));

        mockMvc.perform(get("/forms/result/final")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(queryFinalFormResultUseCase, times(1)).execute(user);
    }

    @Test
    void 원서의_최종_결과를_확인할_때_원서를_접수하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(queryFinalFormResultUseCase).execute(user);

        mockMvc.perform(get("/forms/result/final")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(queryFinalFormResultUseCase, times(1)).execute(user);
    }

    @Test
    void 수험표를_발급받는다() throws Exception {
        User user = UserFixture.createUser();
        MockMultipartFile file = new MockMultipartFile(
                "admission-ticket", "admission-ticket.pdf",
                MediaType.APPLICATION_PDF_VALUE, "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(generateAdmissionTicketUseCase.execute(user)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/forms/admission-ticket")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_PDF)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(generateAdmissionTicketUseCase, times(1)).execute(user);
    }

    @Test
    void 수험표를_발급받을_때_불합격자라면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new InvalidFormStatusException()).given(generateAdmissionTicketUseCase).execute(user);

        mockMvc.perform(get("/forms/admission-ticket")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(generateAdmissionTicketUseCase, times(1)).execute(user);
    }

    @Test
    void 수험표를_발급받을_때_원서를_접수하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(generateAdmissionTicketUseCase).execute(user);

        mockMvc.perform(get("/forms/admission-ticket")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(generateAdmissionTicketUseCase, times(1)).execute(user);
    }

    @Test
    void 접수증을_발급받는다() throws Exception {
        User user = UserFixture.createUser();
        MockMultipartFile file = new MockMultipartFile(
                "proof-of-application", "proof-of-application.pdf",
                MediaType.APPLICATION_PDF_VALUE, "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(generateAdmissionTicketUseCase.execute(user)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/forms/proof-of-application")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_PDF)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        )
                ));

        verify(generateProofOfApplicationUseCase, times(1)).execute(user);
    }

    @Test
    void 접수증을_발급받을_때_원서상태가_최종제출이_아니라면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new InvalidFormStatusException()).given(generateProofOfApplicationUseCase).execute(user);

        mockMvc.perform(get("/forms/proof-of-application")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(restDocs.document());

        verify(generateProofOfApplicationUseCase, times(1)).execute(user);
    }

    @Test
    void 접수증을_발급받을_때_원서를_접수하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(generateProofOfApplicationUseCase).execute(user);

        mockMvc.perform(get("/forms/proof-of-application")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());

        verify(generateProofOfApplicationUseCase, times(1)).execute(user);
    }
}