package com.bamdoliro.maru.shared.util;

import com.bamdoliro.maru.application.auth.LogInUseCase;
import com.bamdoliro.maru.application.auth.LogOutUseCase;
import com.bamdoliro.maru.application.auth.RefreshTokenUseCase;
import com.bamdoliro.maru.application.fair.AttendAdmissionFairUseCase;
import com.bamdoliro.maru.application.fair.CreateAdmissionFairUseCase;
import com.bamdoliro.maru.application.fair.QueryFairListUseCase;
import com.bamdoliro.maru.application.form.ApproveFormUseCase;
import com.bamdoliro.maru.application.form.DownloadSecondRoundScoreFormatUseCase;
import com.bamdoliro.maru.application.form.DraftFormUseCase;
import com.bamdoliro.maru.application.form.ExportFormUseCase;
import com.bamdoliro.maru.application.form.GenerateAdmissionTicketUseCase;
import com.bamdoliro.maru.application.form.QueryAllFormUseCase;
import com.bamdoliro.maru.application.form.QueryDraftFormUseCase;
import com.bamdoliro.maru.application.form.QueryFinalFormResultUseCase;
import com.bamdoliro.maru.application.form.QueryFirstFormResultUseCase;
import com.bamdoliro.maru.application.form.QueryFormStatusUseCase;
import com.bamdoliro.maru.application.form.QueryFormUseCase;
import com.bamdoliro.maru.application.form.QuerySubmittedFormUseCase;
import com.bamdoliro.maru.application.form.ReceiveFormUseCase;
import com.bamdoliro.maru.application.form.RejectFormUseCase;
import com.bamdoliro.maru.application.form.SubmitFinalFormUseCase;
import com.bamdoliro.maru.application.form.SubmitFormUseCase;
import com.bamdoliro.maru.application.form.UpdateFormUseCase;
import com.bamdoliro.maru.application.form.UpdateSecondRoundScoreUseCase;
import com.bamdoliro.maru.application.form.UploadFormUseCase;
import com.bamdoliro.maru.application.form.UploadIdentificationPictureUseCase;
import com.bamdoliro.maru.application.notice.CreateNoticeUseCase;
import com.bamdoliro.maru.application.notice.QueryNoticeListUseCase;
import com.bamdoliro.maru.application.notice.QueryNoticeUseCase;
import com.bamdoliro.maru.application.notice.UpdateNoticeUseCase;
import com.bamdoliro.maru.application.question.CreateQuestionUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionListUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionUseCase;
import com.bamdoliro.maru.application.question.UpdateQuestionUseCase;
import com.bamdoliro.maru.application.school.SearchSchoolUseCase;
import com.bamdoliro.maru.application.user.SendEmailVerificationUseCase;
import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.neis.SearchSchoolService;
import com.bamdoliro.maru.presentation.auth.AuthController;
import com.bamdoliro.maru.presentation.fair.FairController;
import com.bamdoliro.maru.presentation.form.DraftFormController;
import com.bamdoliro.maru.presentation.form.FormController;
import com.bamdoliro.maru.presentation.notice.NoticeController;
import com.bamdoliro.maru.presentation.question.QuestionController;
import com.bamdoliro.maru.presentation.school.SchoolController;
import com.bamdoliro.maru.presentation.user.UserController;
import com.bamdoliro.maru.shared.auth.AuthenticationArgumentResolver;
import com.bamdoliro.maru.shared.auth.AuthenticationExtractor;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import com.bamdoliro.maru.shared.response.SharedController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@Disabled
@WebMvcTest({
        UserController.class,
        AuthController.class,
        SharedController.class,
        SchoolController.class,
        QuestionController.class,
        FormController.class,
        NoticeController.class,
        DraftFormController.class,
        FairController.class
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @MockBean
    protected SignUpUserUseCase signUpUserUseCase;

    @MockBean
    protected LogInUseCase logInUseCase;

    @MockBean
    protected RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    protected SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @MockBean
    protected SearchSchoolUseCase searchSchoolUseCase;

    @MockBean
    protected CreateQuestionUseCase createQuestionUseCase;

    @MockBean
    protected UpdateQuestionUseCase updateQuestionUseCase;

    @MockBean
    protected QueryQuestionListUseCase queryQuestionListUseCase;

    @MockBean
    protected SubmitFinalFormUseCase submitFinalFormUseCase;

    @MockBean
    protected SubmitFormUseCase submitFormUseCase;

    @MockBean
    protected ApproveFormUseCase approveFormUseCase;

    @MockBean
    protected RejectFormUseCase rejectFormUseCase;

    @MockBean
    protected ReceiveFormUseCase receiveFormUseCase;

    @MockBean
    protected QuerySubmittedFormUseCase querySubmittedFormUseCase;

    @MockBean
    protected QueryFormUseCase queryFormUseCase;

    @MockBean
    protected QueryFormStatusUseCase queryFormStatusUseCase;

    @MockBean
    protected UpdateFormUseCase updateFormUseCase;

    @MockBean
    protected UploadIdentificationPictureUseCase uploadIdentificationPictureUseCase;

    @MockBean
    protected UploadFormUseCase uploadFormUseCase;

    @MockBean
    protected ExportFormUseCase exportFormUseCase;

    @MockBean
    protected QueryAllFormUseCase queryAllFormUseCase;

    @MockBean
    protected QueryNoticeUseCase queryNoticeUseCase;

    @MockBean
    protected QueryNoticeListUseCase queryNoticeListUseCase;

    @MockBean
    protected CreateNoticeUseCase createNoticeUseCase;

    @MockBean
    protected UpdateNoticeUseCase updateNoticeUseCase;

    @MockBean
    protected LogOutUseCase logOutUseCase;

    @MockBean
    protected DraftFormUseCase draftFormUseCase;

    @MockBean
    protected QueryDraftFormUseCase queryDraftFormUseCase;

    @MockBean
    protected QueryFirstFormResultUseCase queryFirstFormResultUseCase;

    @MockBean
    protected QueryFinalFormResultUseCase queryFinalFormResultUseCase;

    @MockBean
    protected GenerateAdmissionTicketUseCase generateAdmissionTicketUseCase;

    @MockBean
    protected DownloadSecondRoundScoreFormatUseCase downloadSecondRoundScoreFormatUseCase;

    @MockBean
    protected UpdateSecondRoundScoreUseCase updateSecondRoundScoreUseCase;

    @MockBean
    protected CreateAdmissionFairUseCase createAdmissionFairUseCase;

    @MockBean
    protected AttendAdmissionFairUseCase attendAdmissionFairUseCase;

    @MockBean
    protected QueryQuestionUseCase queryQuestionUseCase;

    @MockBean
    protected QueryFairListUseCase queryFairListUseCase;


    @MockBean
    protected TokenService tokenService;

    @MockBean
    protected SearchSchoolService searchSchoolService;

    @MockBean
    protected SendEmailService sendEmailService;


    @MockBean
    protected JwtProperties jwtProperties;

    @MockBean
    protected AuthenticationArgumentResolver authenticationArgumentResolver;

    @MockBean
    protected AuthenticationExtractor authenticationExtractor;


    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}