package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.CannotUpdateNotRejectedFormException;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.exception.InvalidFileException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.exception.FailedToExportPdfException;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.ImageSizeMismatchException;
import com.bamdoliro.maru.infrastructure.s3.exception.MediaTypeMismatchException;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFinalFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormResultResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FormControllerTest extends RestDocsTestSupport {

    @Test
    void 원서를_제출한다() throws Exception {
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
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
                                        .description("<<form-type,원서 유형>>"),
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
                                        .description("지원자 생년월일 (yyyy-MM-dd)"),
                                fieldWithPath("applicant.gender")
                                        .type(JsonFieldType.STRING)
                                        .description("<<gender,지원자 성별>>"),
                                fieldWithPath("parent.name")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 이름"),
                                fieldWithPath("parent.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 전화번호"),
                                fieldWithPath("parent.relation")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 관계"),
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
                                        .description("<<graduation-type,졸업 유형>>"),
                                fieldWithPath("education.graduationYear")
                                        .type(JsonFieldType.STRING)
                                        .description("졸업 연도, 합격 연도"),
                                fieldWithPath("education.schoolName")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 이름  (없는 경우 null)"),
                                fieldWithPath("education.schoolLocation")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 지역 (없는 경우 null)"),
                                fieldWithPath("education.schoolAddress")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 주소지 (없는 경우 null)"),
                                fieldWithPath("education.schoolCode")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 코드 (없는 경우 null)"),
                                fieldWithPath("education.teacherName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 (없는 경우 null)"),
                                fieldWithPath("education.teacherPhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.teacherMobilePhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 휴대전화번호 (없는 경우 null)"),
                                fieldWithPath("grade.subjectList[].subjectName")
                                        .type(JsonFieldType.STRING)
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
                                fieldWithPath("grade.subjectList[].score")
                                        .type(JsonFieldType.NUMBER)
                                        .description("검정고시인 경우 점수 (검정고시가 아닐시 무조건 null)")
                                        .optional(),
                                fieldWithPath("grade.certificateList[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("<<certificate,자격증 리스트>>"),
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
    void 중졸_껌정고시_합격자가_원서를_제출한다() throws Exception {
        SubmitFormRequest request = FormFixture.createQualificationExaminationFormRequest(FormType.MEISTER_TALENT);
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
    void 원서를_제출할_때_이미_제출한_원서가_있으면_에러가_발생한다() throws Exception {
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormAlreadySubmittedException()).when(submitFormUseCase).execute(any(User.class), any(SubmitFormRequest.class));


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
    void 원서를_제출할_때_잘못된_형식의_요청을_보내면_에러가_발생한다() throws Exception {
        SubmitFormRequest request = new SubmitFormRequest();
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

        verify(submitFormUseCase, never()).execute(any(User.class), any(SubmitFormRequest.class));
    }

    @Test
    void 원서를_최종_제출한다() throws Exception {
        SubmitFinalFormRequest request = new SubmitFinalFormRequest("https://maru.bamdoliro.com/form.pdf");
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(submitFinalFormUseCase).execute(any(User.class), any(SubmitFinalFormRequest.class));

        mockMvc.perform(patch("/form")
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
                        requestFields(
                                fieldWithPath("formUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("원서 pdf 파일의 url")
                        )
                ));

        verify(submitFinalFormUseCase, times(1)).execute(any(User.class), any(SubmitFinalFormRequest.class));
    }

    @Test
    void 원서를_최종_제출할_때_이미_제출한_원서라면_에러가_발생한다() throws Exception {
        SubmitFinalFormRequest request = new SubmitFinalFormRequest("https://maru.bamdoliro.com/form.pdf");
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormAlreadySubmittedException()).when(submitFinalFormUseCase).execute(any(User.class), any(SubmitFinalFormRequest.class));

        mockMvc.perform(patch("/form")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isConflict())

                .andDo(restDocs.document());

        verify(submitFinalFormUseCase, times(1)).execute(any(User.class), any(SubmitFinalFormRequest.class));
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
    void 원서를_접수한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(receiveFormUseCase).execute(formId);


        mockMvc.perform(patch("/form/{form-id}/receive", formId)
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
                                        .description("접수할 원서의 id")
                        )
                ));

        verify(receiveFormUseCase, times(1)).execute(formId);
    }

    @Test
    void 원서를_접수할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(receiveFormUseCase).execute(formId);


        mockMvc.perform(patch("/form/{form-id}/receive", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(receiveFormUseCase, times(1)).execute(formId);
    }

    @Test
    void 검토해야_하는_원서를_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(querySubmittedFormUseCase.execute()).willReturn(List.of(
                FormFixture.createFormSimpleResponse(FormStatus.FINAL_SUBMITTED),
                FormFixture.createFormSimpleResponse(FormStatus.REJECTED),
                FormFixture.createFormSimpleResponse(FormStatus.FINAL_SUBMITTED)
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
    void 원서를_상세_조회한다() throws Exception {
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
    void 원서를_상세_조회할_때_원서가_없으면_에러가_발생한다() throws Exception {
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
    void 원서를_상세_조회할_때_본인의_원서가_아니면_에러가_발생한다() throws Exception {
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
    void 원서_상태를_조회한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormStatusUseCase.execute(user)).willReturn(FormFixture.createFormSimpleResponse(FormStatus.APPROVED));


        mockMvc.perform(get("/form/status")
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

        verify(queryFormStatusUseCase, times(1)).execute(user);
    }

    @Test
    void 원서_상태를_조회할_때_원서가_없으면_에러가_발생한다() throws Exception {
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormStatusUseCase.execute(user)).willThrow(new FormNotFoundException());


        mockMvc.perform(get("/form/status")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }

    @Test
    void 원서를_수정한다() throws Exception {
        Long formId = 1L;
        UpdateFormRequest request = FormFixture.createUpdateFormRequest(FormType.REGULAR);
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
                                        .description("<<form-type,원서 유형>>"),
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
                                        .description("<<gender,지원자 성별>>"),
                                fieldWithPath("parent.name")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 이름"),
                                fieldWithPath("parent.phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 전화번호"),
                                fieldWithPath("parent.relation")
                                        .type(JsonFieldType.STRING)
                                        .description("보호자 관계"),
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
                                        .description("<<graduation-type,졸업 유형>>"),
                                fieldWithPath("education.graduationYear")
                                        .type(JsonFieldType.STRING)
                                        .description("졸업 연도"),
                                fieldWithPath("education.schoolName")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 이름"),
                                fieldWithPath("education.schoolLocation")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 지역"),
                                fieldWithPath("education.schoolAddress")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 주소지"),
                                fieldWithPath("education.schoolCode")
                                        .type(JsonFieldType.STRING)
                                        .description("출신 학교 코드"),
                                fieldWithPath("education.teacherName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 (없는 경우 null)"),
                                fieldWithPath("education.teacherPhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 전화번호 (없는 경우 null)"),
                                fieldWithPath("education.teacherMobilePhoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("작성 교사 휴대전화번호 (없는 경우 null)"),
                                fieldWithPath("grade.subjectList[].subjectName")
                                        .type(JsonFieldType.STRING)
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
                                fieldWithPath("grade.subjectList[].score")
                                        .type(JsonFieldType.NUMBER)
                                        .description("검정고시인 경우 점수 (검정고시가 아닐시 무조건 null)")
                                        .optional(),
                                fieldWithPath("grade.certificateList[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("<<certificate,자격증 리스트>>"),
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

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FormNotFoundException()).when(updateFormUseCase).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createUpdateFormRequest(FormType.REGULAR)))
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_본인의_원서가_아니면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new AuthorityMismatchException()).when(updateFormUseCase).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createUpdateFormRequest(FormType.REGULAR)))
                )

                .andExpect(status().isUnauthorized())

                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));
    }

    @Test
    void 원서를_수정할_때_반려된_원서가_아니면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new CannotUpdateNotRejectedFormException()).when(updateFormUseCase).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));


        mockMvc.perform(put("/form/{form-id}", formId)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(FormFixture.createUpdateFormRequest(FormType.REGULAR)))
                )

                .andExpect(status().isConflict())

                .andDo(restDocs.document());

        verify(updateFormUseCase, times(1)).execute(any(User.class), anyLong(), any(UpdateFormRequest.class));
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
        given(uploadIdentificationPictureUseCase.execute(user, image)).willReturn(new UploadResponse("https://example.com/image.png"));

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestParts(
                                partWithName("image")
                                        .description("증명사진 파일, 3*4cm, 117*156px, 최대 2MB")
                        )
                ));

        verify(uploadIdentificationPictureUseCase, times(1)).execute(user, image);
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
        doThrow(new FailedToSaveException()).when(uploadIdentificationPictureUseCase).execute(user, image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isInternalServerError())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(user, image);
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
        doThrow(new ImageSizeMismatchException()).when(uploadIdentificationPictureUseCase).execute(user, image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(user, image);
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
        doThrow(new EmptyFileException()).when(uploadIdentificationPictureUseCase).execute(user, image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(user, image);
    }

    @Test
    void 증명_사진을_업로드할_때_파일이_용량_제한을_넘으면_에러가_발생한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[(int) (20 * MB + 1)]
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FileSizeLimitExceededException()).when(uploadIdentificationPictureUseCase).execute(user, image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(user, image);
    }

    @Test
    void 증명_사진을_업로드할_때_콘텐츠_타입이_다르다면_에러가_발생한다() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<pdf>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new MediaTypeMismatchException()).when(uploadIdentificationPictureUseCase).execute(user, image);

        mockMvc.perform(multipart("/form/identification-picture")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isUnsupportedMediaType())

                .andDo(restDocs.document());

        verify(uploadIdentificationPictureUseCase, times(1)).execute(user, image);
    }

    @Test
    void 원서_서류를_업로드한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadFormUseCase.execute(user, file)).willReturn(new UploadResponse("https://example.com/file.pdf"));

        mockMvc.perform(multipart("/form/form-document")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestParts(
                                partWithName("file")
                                        .description("원서 및 서류 파일, 최대 10MB")
                        )
                ));

        verify(uploadFormUseCase, times(1)).execute(user, file);
    }

    @Test
    void 원서_서류_업로드가_실패한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FailedToSaveException()).when(uploadFormUseCase).execute(user, file);

        mockMvc.perform(multipart("/form/form-document")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isInternalServerError())

                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(user, file);
    }

    @Test
    void 원서_서류를_업로드할_때_파일이_비었으면_에러가_발생한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new EmptyFileException()).when(uploadFormUseCase).execute(user, file);

        mockMvc.perform(multipart("/form/form-document")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(user, file);
    }

    @Test
    void 원서_서류를_업로드할_때_파일이_용량_제한을_넘으면_에러가_발생한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new FileSizeLimitExceededException()).when(uploadFormUseCase).execute(user, file);

        mockMvc.perform(multipart("/form/form-document")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(user, file);
    }

    @Test
    void 원서_서류를_업로드할_때_콘텐츠_타입이_다르다면_에러가_발생한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.IMAGE_PNG_VALUE,
                "<<file>>".getBytes()
        );
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new MediaTypeMismatchException()).when(uploadFormUseCase).execute(user, file);

        mockMvc.perform(multipart("/form/form-document")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                )

                .andExpect(status().isUnsupportedMediaType())

                .andDo(restDocs.document());

        verify(uploadFormUseCase, times(1)).execute(user, file);
    }

    @Test
    void 원서를_pdf로_다운받는다() throws Exception {
        User user = UserFixture.createUser();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportFormUseCase.execute(user)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/export")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_PDF)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
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

        mockMvc.perform(get("/form/export")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
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

        mockMvc.perform(get("/form/export")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
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

        mockMvc.perform(get("/form/export")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isInternalServerError())

                .andDo(restDocs.document());

        verify(exportFormUseCase, times(1)).execute(user);
    }

    @Test
    void 원서를_전체_조회한다() throws Exception {
        User user = UserFixture.createUser();
        List<FormSimpleResponse> responseList = List.of(
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED),
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED),
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED),
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED),
                FormFixture.createFormSimpleResponse(FormStatus.SUBMITTED)
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryAllFormUseCase.execute(FormStatus.SUBMITTED, FormType.Category.REGULAR)).willReturn(responseList);

        mockMvc.perform(get("/form")
                        .param("status", FormStatus.SUBMITTED.name())
                        .param("type", FormType.Category.REGULAR.name())
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("status")
                                        .description("<<form-status,원서 상태 (null인 경우 전체 조회)>>")
                                        .optional(),
                                parameterWithName("type")
                                        .description("<<form-type,원서 유형 (null인 경우 전체 조회)>>")
                                        .optional()
                        )
                ));

        verify(queryAllFormUseCase, times(1)).execute(FormStatus.SUBMITTED, FormType.Category.REGULAR);
    }

    @Test
    void 원서의_1차_결과를_확인한다() throws Exception {
        Form form = FormFixture.createForm(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFirstFormResultUseCase.execute(user)).willReturn(new FormResultResponse(form, true));

        mockMvc.perform(get("/form/result/first")
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

        verify(queryFirstFormResultUseCase, times(1)).execute(user);
    }

    @Test
    void 원서의_1차_결과를_확인할_때_원서를_접수하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(queryFirstFormResultUseCase).execute(user);

        mockMvc.perform(get("/form/result/first")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
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

        mockMvc.perform(get("/form/result/final")
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

        verify(queryFinalFormResultUseCase, times(1)).execute(user);
    }

    @Test
    void 원서의_최종_결과를_확인할_때_원서를_접수하지_않았다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(queryFinalFormResultUseCase).execute(user);

        mockMvc.perform(get("/form/result/final")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
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
                "admission-ticket",
                "admission-ticket.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(generateAdmissionTicketUseCase.execute(user)).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/admission-ticket")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_PDF)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
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

        mockMvc.perform(get("/form/admission-ticket")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
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

        mockMvc.perform(get("/form/admission-ticket")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(generateAdmissionTicketUseCase, times(1)).execute(user);
    }

    @Test
    void 정상적으로_2차_전형_점수_양식을_다운로드한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "2차전형점수양식",
                "2차전형점수양식.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(downloadSecondRoundScoreFormatUseCase.execute()).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/second-round/format")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(downloadSecondRoundScoreFormatUseCase, times(1)).execute();
    }

    @Test
    void 정상적으로_2차_전형_점수를_입력한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "xlsx",
                "2차전형점수양식.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(updateSecondRoundScoreUseCase).execute(any(MultipartFile.class));

        mockMvc.perform(multipartPatch("/form/second-round/score")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestParts(
                                partWithName("xlsx")
                                        .description("2차 전형 점수 양식 엑셀 파일")
                        )
                ));

        verify(updateSecondRoundScoreUseCase, times(1)).execute(any(MultipartFile.class));
    }

    @Test
    void 잘못된_양식의_2차_전형_점수를_입력하면_에러가_발생한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "xlsx",
                "2차전형점수양식.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        doThrow(new InvalidFileException()).when(updateSecondRoundScoreUseCase).execute(any(MultipartFile.class));

        mockMvc.perform(multipartPatch("/form/second-round/score")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isBadRequest())

                .andDo(restDocs.document());

        verify(updateSecondRoundScoreUseCase, times(1)).execute(any(MultipartFile.class));
    }

    @Test
    void 정상적으로_최종_합격자를_다운로드한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "최종합격자",
                "최종합격자.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportFinalPassedFormUseCase.execute()).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/xlsx/final-passed")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(exportFinalPassedFormUseCase, times(1)).execute();
    }

    @Test
    void 정상적으로_1차전형_결과를_다운로드한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "1차전형결과",
                "1차전형결과.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportFinalPassedFormUseCase.execute()).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/xlsx/first-round")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(exportFirstRoundResultUseCase, times(1)).execute();
    }

    @Test
    void 정상적으로_2차전형_결과를_다운로드한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "2차전형결과",
                "2차전형결과.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportFinalPassedFormUseCase.execute()).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/xlsx/second-round")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(exportSecondRoundResultUseCase, times(1)).execute();
    }

    @Test
    void 정상적으로_입학전형_전체_결과를_다운로드한다() throws Exception {
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "전체결과",
                "전체결과.xlsx",
                String.valueOf(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
                "<<file>>".getBytes()
        );

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(exportFinalPassedFormUseCase.execute()).willReturn(new ByteArrayResource(file.getBytes()));

        mockMvc.perform(get("/form/xlsx/result")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(exportResultUseCase, times(1)).execute();
    }

    @Test
    void 정상적으로_2차_합격_여부를_입력한다() throws Exception {
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willDoNothing().given(passOrFailFormUseCase).execute(any(PassOrFailFormListRequest.class));

        PassOrFailFormListRequest request = new PassOrFailFormListRequest(
                List.of(
                        new PassOrFailFormRequest(3L, true),
                        new PassOrFailFormRequest(2L, false),
                        new PassOrFailFormRequest(5L, true),
                        new PassOrFailFormRequest(4L, false),
                        new PassOrFailFormRequest(1L, true)
                )
        );

        mockMvc.perform(patch("/form/second-round/result")
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
                                fieldWithPath("formList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("2차 전형 결과를 입력할 원서 목록"),
                                fieldWithPath("formList[].formId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("원서 id"),
                                fieldWithPath("formList[].pass")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("합격 여부")
                        )
                ));

        verify(passOrFailFormUseCase, times(1)).execute(any(PassOrFailFormListRequest.class));
    }

    @Test
    void 어드민이_2차_합격_여부를_입력할_때_존재하지_않는_원서를_입력했다면_에러가_발생한다() throws Exception {
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        willThrow(new FormNotFoundException()).given(passOrFailFormUseCase).execute(any(PassOrFailFormListRequest.class));

        PassOrFailFormListRequest request = new PassOrFailFormListRequest(
                List.of(
                        new PassOrFailFormRequest(390L, true)
                )
        );

        mockMvc.perform(patch("/form/second-round/result")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(passOrFailFormUseCase, times(1)).execute(any(PassOrFailFormListRequest.class));
    }

    @Test
    void 선택한_원서의_원서url을_조회한다() throws Exception {
        User user = UserFixture.createAdminUser();
        List<Long> idList = List.of(1L, 2L, 3L, 4L, 5L);

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUrlUseCase.execute(idList)).willReturn(
                List.of(FormFixture.createFormUrlResponse(),
                        FormFixture.createFormUrlResponse(),
                        FormFixture.createFormUrlResponse(),
                        FormFixture.createFormUrlResponse(),
                        FormFixture.createFormUrlResponse())
        );

        mockMvc.perform(get("/form/form-url")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .param("id-list", "1,2,3,4,5")
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        queryParameters(
                                parameterWithName("id-list")
                                        .description("조회할 원서 id 목록")
                        )
                ));

        verify(queryFormUrlUseCase, times(1)).execute(idList);
    }
}