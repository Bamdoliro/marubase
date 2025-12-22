package com.bamdoliro.maru.shared.util;

import com.bamdoliro.maru.application.analysis.QueryGenderRatioUseCase;
import com.bamdoliro.maru.application.analysis.QueryGradeDistributionUseCase;
import com.bamdoliro.maru.application.analysis.QueryNumberOfApplicantsUseCase;
import com.bamdoliro.maru.application.analysis.QuerySchoolStatusUseCase;
import com.bamdoliro.maru.application.auth.LogInUseCase;
import com.bamdoliro.maru.application.auth.LogOutUseCase;
import com.bamdoliro.maru.application.auth.RefreshTokenUseCase;
import com.bamdoliro.maru.application.fair.*;
import com.bamdoliro.maru.application.form.*;
import com.bamdoliro.maru.application.message.SendMessageUseCase;
import com.bamdoliro.maru.application.notice.*;
import com.bamdoliro.maru.application.question.*;
import com.bamdoliro.maru.application.school.SearchSchoolUseCase;
import com.bamdoliro.maru.application.user.*;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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


    @MockitoBean
    protected SignUpUserUseCase signUpUserUseCase;

    @MockitoBean
    protected LogInUseCase logInUseCase;

    @MockitoBean
    protected UpdatePasswordUseCase updatePasswordUseCase;

    @MockitoBean
    protected DeleteUserUseCase deleteUserUseCase;

    @MockitoBean
    protected RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    protected SendVerificationUseCase sendVerificationUseCase;

    @MockitoBean
    protected SearchSchoolUseCase searchSchoolUseCase;

    @MockitoBean
    protected CreateQuestionUseCase createQuestionUseCase;

    @MockitoBean
    protected UpdateQuestionUseCase updateQuestionUseCase;

    @MockitoBean
    protected QueryQuestionListUseCase queryQuestionListUseCase;

    @MockitoBean
    protected SubmitFinalFormUseCase submitFinalFormUseCase;

    @MockitoBean
    protected SubmitFormUseCase submitFormUseCase;

    @MockitoBean
    protected ApproveFormUseCase approveFormUseCase;

    @MockitoBean
    protected RejectFormUseCase rejectFormUseCase;

    @MockitoBean
    protected ReceiveFormUseCase receiveFormUseCase;

    @MockitoBean
    protected EnterFormUseCase enterFormUseCase;

    @MockitoBean
    protected QuerySubmittedFormUseCase querySubmittedFormUseCase;

    @MockitoBean
    protected QueryFormUseCase queryFormUseCase;

    @MockitoBean
    protected QueryFormStatusUseCase queryFormStatusUseCase;

    @MockitoBean
    protected UpdateFormUseCase updateFormUseCase;

    @MockitoBean
    protected UploadIdentificationPictureUseCase uploadIdentificationPictureUseCase;

    @MockitoBean
    protected UploadFormUseCase uploadFormUseCase;

    @MockitoBean
    protected ExportFormUseCase exportFormUseCase;

    @MockitoBean
    protected QueryAllFormUseCase queryAllFormUseCase;

    @MockitoBean
    protected QueryNoticeUseCase queryNoticeUseCase;

    @MockitoBean
    protected QueryNoticeListUseCase queryNoticeListUseCase;

    @MockitoBean
    protected CreateNoticeUseCase createNoticeUseCase;

    @MockitoBean
    protected UploadFileUseCase uploadFileUseCase;

    @MockitoBean
    protected UpdateNoticeUseCase updateNoticeUseCase;

    @MockitoBean
    protected LogOutUseCase logOutUseCase;

    @MockitoBean
    protected DraftFormUseCase draftFormUseCase;

    @MockitoBean
    protected QueryDraftFormUseCase queryDraftFormUseCase;

    @MockitoBean
    protected QueryFirstFormResultUseCase queryFirstFormResultUseCase;

    @MockitoBean
    protected QueryFinalFormResultUseCase queryFinalFormResultUseCase;

    @MockitoBean
    protected GenerateAdmissionTicketUseCase generateAdmissionTicketUseCase;

    @MockitoBean
    protected GenerateProofOfApplicationUseCase generateProofOfApplicationUseCase;

    @MockitoBean
    protected DownloadSecondRoundScoreFormatUseCase downloadSecondRoundScoreFormatUseCase;

    @MockitoBean
    protected ExportFirstScoreUseCase exportFirstScoreUseCase;

    @MockitoBean
    protected UpdateSecondRoundScoreUseCase updateSecondRoundScoreUseCase;

    @MockitoBean
    protected CreateAdmissionFairUseCase createAdmissionFairUseCase;

    @MockitoBean
    protected AttendAdmissionFairUseCase attendAdmissionFairUseCase;

    @MockitoBean
    protected QueryQuestionUseCase queryQuestionUseCase;

    @MockitoBean
    protected QueryFairListUseCase queryFairListUseCase;

    @MockitoBean
    protected QueryFairDetailUseCase queryFairDetailUseCase;

    @MockitoBean
    protected ExportAttendeeListUseCase exportAttendeeListUseCase;

    @MockitoBean
    protected VerifyUseCase verifyUseCase;

    @MockitoBean
    protected DeleteNoticeUseCase deleteNoticeUseCase;

    @MockitoBean
    protected DeleteQuestionUseCase deleteQuestionUseCase;

    @MockitoBean
    protected ExportSubjectGradeDetailUseCase exportSubjectGradeDetailUseCase;

    @MockitoBean
    protected ExportFinalPassedFormUseCase exportFinalPassedFormUseCase;

    @MockitoBean
    protected ExportFirstRoundResultUseCase exportFirstRoundResultUseCase;

    @MockitoBean
    protected ExportSecondRoundResultUseCase exportSecondRoundResultUseCase;

    @MockitoBean
    protected ExportResultUseCase exportResultUseCase;

    @MockitoBean
    protected PassOrFailFormUseCase passOrFailFormUseCase;

    @MockitoBean
    protected QueryFormUrlUseCase queryFormUrlUseCase;

    @MockitoBean
    protected SelectSecondPassUseCase selectSecondPassUseCase;

    @MockitoBean
    protected SendMessageUseCase sendMessageUseCase;

    @MockitoBean
    protected QueryNumberOfApplicantsUseCase queryNumberOfApplicantsUseCase;

    @MockitoBean
    protected QueryGradeDistributionUseCase queryGradeDistributionUseCase;

    @MockitoBean
    protected QueryGenderRatioUseCase queryGenderRatioUseCase;

    @MockitoBean
    protected QuerySchoolStatusUseCase querySchoolStatusUseCase;


    @MockitoBean
    protected TokenService tokenService;

    @MockitoBean
    protected SearchSchoolService searchSchoolService;

    @MockitoBean
    protected SendMessageService sendMessageService;


    @MockitoBean
    protected JwtProperties jwtProperties;

    @MockitoBean
    protected AuthenticationArgumentResolver authenticationArgumentResolver;

    @MockitoBean
    protected AuthenticationExtractor authenticationExtractor;

    @MockitoBean
    protected GenerateAllAdmissionTicketUseCase generateAllAdmissionTicketUseCase;

    @MockitoBean
    protected DownloadAdmissionAndPledgeFormatUseCase downloadAdmissionAndPledgeFormatUseCase;

    @MockitoBean
    protected UploadAdmissionAndPledgeUseCase uploadAdmissionAndPledgeUseCase;

    @MockitoBean
    protected QueryAdmissionAndPledgeUseCase queryAdmissionAndPledgeUseCase;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}