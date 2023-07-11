package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.CannotUpdateNotRejectedFormException;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.ImageSizeMismatchException;
import com.bamdoliro.maru.infrastructure.s3.exception.InvalidFileNameException;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.bamdoliro.maru.shared.constants.FileConstants.MB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FormControllerTest extends RestDocsTestSupport {

    @Test
    void 원서를_접수한다() throws Exception {
        FormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(submitFormUseCase).execute(user, request);


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
                                fieldWithPath("applicant.identificationPictureUri")
                                        .type(JsonFieldType.STRING)
                                        .description("증명사진 URI"),
                                fieldWithPath("applicant.name")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 이름"),
                                fieldWithPath("applicant.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 전화번호"),
                                fieldWithPath("applicant.birthday")
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
                                fieldWithPath("education.schoolName")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 이름 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.schoolLocation")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 지역 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.schoolCode")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 코드 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.teacherName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 (없는 경우 null)"),
                                fieldWithPath("education.teacherPhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 전화번호 (없는 경우 null)"),
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
                                        .description("1600자 이내의 학업계획서"),
                                fieldWithPath("formUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("제출 서류 URL")
                        )
                ));
    }

    @Test
    void 중졸_껌정고시_합격자가_원서를_접수한다() throws Exception {
        FormRequest request = FormFixture.createQualificationExaminationFormRequest(FormType.MEISTER_TALENT);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(submitFormUseCase).execute(user, request);


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
        FormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormAlreadySubmittedException()).when(submitFormUseCase).execute(any(User.class), any(FormRequest.class));


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
        FormRequest request = new FormRequest();
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);


        mockMvc.perform(post("/form")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(submitFormUseCase, never()).execute(any(User.class), any(FormRequest.class));
    }

    @Test
    void 원서를_승인한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(approveFormUseCase).execute(formId);


        mockMvc.perform(patch("/form/{form-id}/approve", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("form-id")
                                        .description("승인할 원서의 id")
                        )
                ));

        verify(approveFormUseCase, times(1)).execute(formId);
    }

    @Test
    void 원서를_승인할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(approveFormUseCase).execute(formId);


        mockMvc.perform(patch("/form/{form-id}/approve", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(approveFormUseCase, times(1)).execute(formId);
    }

    @Test
    void 원서를_반려한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(rejectFormUseCase).execute(formId);


        mockMvc.perform(patch("/form/{form-id}/reject", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("form-id")
                                        .description("반려할 원서의 id")
                        )
                ));

        verify(rejectFormUseCase, times(1)).execute(formId);
    }

    @Test
    void 원서를_반려할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(rejectFormUseCase).execute(formId);


        mockMvc.perform(patch("/form/{form-id}/reject", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(rejectFormUseCase, times(1)).execute(formId);
    }

    @Test
    void 검토해야_하는_원서를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(querySubmittedFormUseCase.execute()).willReturn(List.of(
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED),
                FormFixture.createFormSimpleResponse(FormStatus.REJECTED),
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED)
        ));


        mockMvc.perform(get("/form/review")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));
    }

    @Test
    void 검토해야_하는_원서가_없으면_빈_리스트를_반환한다() throws Exception {
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(querySubmittedFormUseCase.execute()).willReturn(List.of());


        mockMvc.perform(get("/form/review")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document());
    }

    @Test
    void 원서를_조회한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUseCase.execute(user, formId)).willReturn(FormFixture.createFormResponse());


        mockMvc.perform(get("/form/{form-id}", formId)
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
                                parameterWithName("form-id")
                                        .description("조회할 원서의 id")
                        )
                ));

        verify(queryFormUseCase, times(1)).execute(user, formId);
    }

    @Test
    void 원서를_조회할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUseCase.execute(user, formId)).willThrow(new FormNotFoundException());


        mockMvc.perform(get("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }

    @Test
    void 원서를_조회할_때_본인의_원서가_아니면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUseCase.execute(user, formId)).willThrow(new AuthorityMismatchException());


        mockMvc.perform(get("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());
    }

    @Test
    void 원서를_수정한다() throws Exception {
        Long formId = 1L;
        FormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(updateFormUseCase).execute(user, formId, request);


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("form-id")
                                        .description("수정할 원서 id")
                        ),
                        requestFields(
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("원서 유형"),
                                fieldWithPath("applicant.identificationPictureUri")
                                        .type(JsonFieldType.STRING)
                                        .description("증명사진 URI"),
                                fieldWithPath("applicant.name")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 이름"),
                                fieldWithPath("applicant.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("지원자 전화번호"),
                                fieldWithPath("applicant.birthday")
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
                                fieldWithPath("education.schoolName")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 이름 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.schoolLocation")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 지역 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.schoolCode")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 코드 (출신 학교가 없는 경우 null)"),
                                fieldWithPath("education.teacherName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 (없는 경우 null)"),
                                fieldWithPath("education.teacherPhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 전화번호 (없는 경우 null)"),
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
                                        .description("1600자 이내의 학업계획서"),
                                fieldWithPath("formUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("제출 서류 URL")
                        )
                ));

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(FormRequest.class));
    }

    @Test
    void 원서를_수정할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(updateFormUseCase).execute(any(User.class), anyLong(), any(FormRequest.class));


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createFormRequest(FormType.REGULAR)))
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(FormRequest.class));
    }

    @Test
    void 원서를_수정할_때_본인의_원서가_아니면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new AuthorityMismatchException()).when(updateFormUseCase).execute(any(User.class), anyLong(), any(FormRequest.class));


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createFormRequest(FormType.REGULAR)))
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(FormRequest.class));
    }

    @Test
    void 원서를_수정할_때_반려된_원서가_아니면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new CannotUpdateNotRejectedFormException()).when(updateFormUseCase).execute(any(User.class), anyLong(), any(FormRequest.class));


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createFormRequest(FormType.REGULAR)))
                )

                .andExpect(status().isConflict())

                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(FormRequest.class));
    }

    @Test
    void 증명_사진을_업로드한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadIdentificationPictureUseCase.execute(image)).willReturn(new UploadResponse("https://example.com/image.png"));

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestParts(
                                partWithName("image")
                                        .description("증명사진 파일, 3*4cm, 117*156px")
                        )
                ));

        verify(uploadIdentificationPictureUseCase, times(1)).execute(image);
    }

    @Test
    void 증명_사진_업로드가_실패한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FailedToSaveException()).when(uploadIdentificationPictureUseCase).execute(image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )

                .andExpect(status().isInternalServerError())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(image);
    }

    @Test
    void 증명_사진을_업로드할_때_사진_크기가_다르면_에러가_발생한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new ImageSizeMismatchException()).when(uploadIdentificationPictureUseCase).execute(image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(image);
    }

    @Test
    void 증명_사진을_업로드할_때_파일_이름이_잘못됐으면_에러가_발생한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "toolonglonglongreallylong.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFileNameException()).when(uploadIdentificationPictureUseCase).execute(image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(image);
    }

    @Test
    void 증명_사진을_업로드할_때_파일이_비었으면_에러가_발생한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new EmptyFileException()).when(uploadIdentificationPictureUseCase).execute(image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(image);
    }

    @Test
    void 증명_사진을_업로드할_때_파일이_용량_제한을_넘으면_에러가_발생한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[(int) (10 * MB + 1)]
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FileSizeLimitExceededException()).when(uploadIdentificationPictureUseCase).execute(image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(image);
    }
}