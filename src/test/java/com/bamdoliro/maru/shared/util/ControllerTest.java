package com.bamdoliro.maru.shared.util;

import com.bamdoliro.maru.application.analysis.QueryGenderRatioUseCase;
import com.bamdoliro.maru.application.analysis.QueryGradeDistributionUseCase;
import com.bamdoliro.maru.application.analysis.QueryNumberOfApplicantsUseCase;
import com.bamdoliro.maru.application.analysis.QuerySchoolStatusUseCase;
import com.bamdoliro.maru.application.auth.LogInUseCase;
import com.bamdoliro.maru.application.auth.LogOutUseCase;
import com.bamdoliro.maru.application.auth.RefreshTokenUseCase;
import com.bamdoliro.maru.application.fair.AttendAdmissionFairUseCase;
import com.bamdoliro.maru.application.fair.CreateAdmissionFairUseCase;
import com.bamdoliro.maru.application.fair.ExportAttendeeListUseCase;
import com.bamdoliro.maru.application.fair.QueryFairDetailUseCase;
import com.bamdoliro.maru.application.fair.QueryFairListUseCase;
import com.bamdoliro.maru.application.form.*;
import com.bamdoliro.maru.application.message.SendMessageUseCase;
import com.bamdoliro.maru.application.notice.*;
import com.bamdoliro.maru.application.question.CreateQuestionUseCase;
import com.bamdoliro.maru.application.question.DeleteQuestionUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionListUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionUseCase;
import com.bamdoliro.maru.application.question.UpdateQuestionUseCase;
import com.bamdoliro.maru.application.school.SearchSchoolUseCase;
import com.bamdoliro.maru.application.user.SendVerificationUseCase;
import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.application.user.UpdatePasswordUseCase;
import com.bamdoliro.maru.application.user.VerifyUseCase;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.neis.SearchSchoolService;
import com.bamdoliro.maru.presentation.analysis.AnalysisController;
import com.bamdoliro.maru.presentation.auth.AuthController;
import com.bamdoliro.maru.presentation.fair.FairController;
import com.bamdoliro.maru.presentation.form.DraftFormController;
import com.bamdoliro.maru.presentation.form.FormController;
import com.bamdoliro.maru.presentation.message.MessageController;
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
@WebMvcTest({UserController.class,
            AuthController.class,
            SharedController.class,
            SchoolController.class,
            QuestionController.class,
            FormController.class,
            NoticeController.class,
            DraftFormController.class,
            FairController.class,
            MessageController.class,
            AnalysisController.class})
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
    protected UpdatePasswordUseCase updatePasswordUseCase;

    @MockBean
    protected RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    protected SendVerificationUseCase sendVerificationUseCase;

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
    protected UploadFileUseCase uploadFileUseCase;

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
    protected GenerateProofOfApplicationUseCase generateProofOfApplicationUseCase;

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
    protected QueryFairDetailUseCase queryFairDetailUseCase;

    @MockBean
    protected ExportAttendeeListUseCase exportAttendeeListUseCase;

    @MockBean
    protected VerifyUseCase verifyUseCase;

    @MockBean
    protected DeleteNoticeUseCase deleteNoticeUseCase;

    @MockBean
    protected DeleteQuestionUseCase deleteQuestionUseCase;

    @MockBean
    protected ExportFinalPassedFormUseCase exportFinalPassedFormUseCase;

    @MockBean
    protected ExportFirstRoundResultUseCase exportFirstRoundResultUseCase;

    @MockBean
    protected ExportSecondRoundResultUseCase exportSecondRoundResultUseCase;

    @MockBean
    protected ExportResultUseCase exportResultUseCase;

    @MockBean
    protected PassOrFailFormUseCase passOrFailFormUseCase;

    @MockBean
    protected QueryFormUrlUseCase queryFormUrlUseCase;

    @MockBean
    protected SelectSecondPassUseCase selectSecondPassUseCase;

    @MockBean
    protected SendMessageUseCase sendMessageUseCase;

    @MockBean
    protected QueryNumberOfApplicantsUseCase queryNumberOfApplicantsUseCase;

    @MockBean
    protected QueryGradeDistributionUseCase queryGradeDistributionUseCase;

    @MockBean
    protected QueryGenderRatioUseCase queryGenderRatioUseCase;

    @MockBean
    protected QuerySchoolStatusUseCase querySchoolStatusUseCase;


    @MockBean
    protected TokenService tokenService;

    @MockBean
    protected SearchSchoolService searchSchoolService;

    @MockBean
    protected SendMessageService sendMessageService;


    @MockBean
    protected JwtProperties jwtProperties;

    @MockBean
    protected AuthenticationArgumentResolver authenticationArgumentResolver;

    @MockBean
    protected AuthenticationExtractor authenticationExtractor;

    @MockBean
    protected UpdateOriginalTypeUseCase updateOriginalTypeUseCase;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}