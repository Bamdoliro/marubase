package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.type.Gender;
import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.form.domain.value.Address;
import com.bamdoliro.maru.domain.form.domain.value.Applicant;
import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import com.bamdoliro.maru.domain.form.domain.value.Document;
import com.bamdoliro.maru.domain.form.domain.value.Education;
import com.bamdoliro.maru.domain.form.domain.value.Grade;
import com.bamdoliro.maru.domain.form.domain.value.Parent;
import com.bamdoliro.maru.domain.form.domain.value.School;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;
import com.bamdoliro.maru.domain.form.domain.value.Teacher;
import com.bamdoliro.maru.presentation.form.dto.request.ApplicantRequest;
import com.bamdoliro.maru.presentation.form.dto.request.AttendanceRequest;
import com.bamdoliro.maru.presentation.form.dto.request.DocumentRequest;
import com.bamdoliro.maru.presentation.form.dto.request.EducationRequest;
import com.bamdoliro.maru.presentation.form.dto.request.GradeRequest;
import com.bamdoliro.maru.presentation.form.dto.request.ParentRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubjectRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormDraftRequest;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.ApplicantResponse;
import com.bamdoliro.maru.presentation.form.dto.response.AttendanceResponse;
import com.bamdoliro.maru.presentation.form.dto.response.DocumentResponse;
import com.bamdoliro.maru.presentation.form.dto.response.EducationResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.presentation.form.dto.response.GradeResponse;
import com.bamdoliro.maru.presentation.form.dto.response.ParentResponse;
import com.bamdoliro.maru.presentation.form.dto.response.SubjectResponse;

import java.time.LocalDate;
import java.util.List;

public class FormFixture {

    public static Form createForm(FormType type) {
        return new Form(
                new Applicant(
                        "https://maru.com/photo.png",
                        "김밤돌",
                        "01012345678",
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        "01012345678",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.EXPECTED,
                        "2021",
                        new School("비전중학교", "경기도", "7631003"),
                        new Teacher("나교사", "0519701234", "01012344321")
                ),
                new Grade(
                        new SubjectList(
                                List.of(
                                new Subject(2, 1, "국어", AchievementLevel.A),
                                new Subject(2, 1, "수학", AchievementLevel.A),
                                new Subject(2, 1, "사회", AchievementLevel.A),
                                new Subject(2, 1, "과학", AchievementLevel.A),
                                new Subject(2, 1, "영어", AchievementLevel.A),
                                new Subject(2, 1, "체육", AchievementLevel.A),
                                new Subject(2, 2, "국어", AchievementLevel.A),
                                new Subject(2, 2, "수학", AchievementLevel.A),
                                new Subject(2, 2, "사회", AchievementLevel.A),
                                new Subject(2, 2, "과학", AchievementLevel.A),
                                new Subject(2, 2, "영어", AchievementLevel.A),
                                new Subject(2, 2, "체육", AchievementLevel.A),
                                new Subject(3, 1, "국어", AchievementLevel.A),
                                new Subject(3, 1, "수학", AchievementLevel.B),
                                new Subject(3, 1, "사회", AchievementLevel.A),
                                new Subject(3, 1, "과학", AchievementLevel.A),
                                new Subject(3, 1, "영어", AchievementLevel.A),
                                new Subject(3, 1, "체육", AchievementLevel.A)
                        )),
                        new Attendance(0, 0, 0, 2),
                        new Attendance(2, 1, 0, 0),
                        new Attendance(0, 0, 1, 0),
                        8,
                        2,
                        1,
                        List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2)
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                type,
                UserFixture.createUser()
        );
    }

    public static Form createQualificationExaminationForm(FormType type) {
        return new Form(
                new Applicant(
                        "https://maru.com/photo.png",
                        "김밤돌",
                        "01012345678",
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        "01012345678",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.QUALIFICATION_EXAMINATION,
                        "2021",
                        null,
                        null
                ),
                new Grade(
                        new SubjectList(List.of(
                                new Subject(1, 1, "국어", AchievementLevel.A),
                                new Subject(1, 1, "수학", AchievementLevel.E),
                                new Subject(1, 1, "사회", AchievementLevel.A),
                                new Subject(1, 1, "과학", AchievementLevel.D),
                                new Subject(1, 1, "영어", AchievementLevel.A)
                        )),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2)
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                type,
                UserFixture.createUser()
        );
    }


    public static SubmitFormDraftRequest createFormRequest(FormType type) {
        return new SubmitFormDraftRequest(
                createApplicantRequest(),
                createParentRequest(),
                new EducationRequest(
                        GraduationType.EXPECTED,
                        "2021",
                        "비전중학교",
                        "경기도",
                        "7631003",
                        "나교사",
                        "0519701234",
                        "01012344321"
                ),
                new GradeRequest(
                        List.of(new SubjectRequest("국어", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("수학", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("사회", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("과학", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("영어", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("체육", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("한문", null, AchievementLevel.A, null)
                        ),
                        new AttendanceRequest(0, 0, 0, 2),
                        new AttendanceRequest(2, 1, 0, 0),
                        new AttendanceRequest(0, 0, 1, 0),
                        8,
                        2,
                        1,
                        List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2)
                ),
                createDocumentRequest(),
                type
        );
    }

    public static SubmitFormDraftRequest createQualificationExaminationFormRequest(FormType type) {
        return new SubmitFormDraftRequest(
                createApplicantRequest(),
                createParentRequest(),
                new EducationRequest(
                        GraduationType.QUALIFICATION_EXAMINATION,
                        "2021",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                new GradeRequest(
                        List.of(new SubjectRequest("국어", AchievementLevel.A, null, null),
                                new SubjectRequest("수학", AchievementLevel.A, null, null),
                                new SubjectRequest("사회", AchievementLevel.A, null, null),
                                new SubjectRequest("과학", AchievementLevel.A, null, null),
                                new SubjectRequest("영어", AchievementLevel.A, null, null),
                                new SubjectRequest("도덕", AchievementLevel.A, null, null)
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(Certificate.CRAFTSMAN_INFORMATION_PROCESSING, Certificate.COMPUTER_SPECIALIST_LEVEL_1)
                ),
                createDocumentRequest(),
                type
        );
    }

    public static UpdateFormRequest createUpdateFormRequest(FormType type) {
        return new UpdateFormRequest(
                createApplicantRequest(),
                createParentRequest(),
                new EducationRequest(
                        GraduationType.EXPECTED,
                        "2021",
                        "비전중학교",
                        "경기도",
                        "7631003",
                        "나교사",
                        "0519701234",
                        "01012344321"
                ),
                new GradeRequest(
                        List.of(new SubjectRequest("국어", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("수학", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("사회", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("과학", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("영어", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("체육", AchievementLevel.A, AchievementLevel.A, AchievementLevel.A),
                                new SubjectRequest("한문", null, AchievementLevel.A, null)
                        ),
                        new AttendanceRequest(0, 0, 0, 2),
                        new AttendanceRequest(2, 1, 0, 0),
                        new AttendanceRequest(0, 0, 1, 0),
                        8,
                        2,
                        1,
                        List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2)
                ),
                createDocumentRequest(),
                type
        );
    }

    public static FormSimpleResponse createFormSimpleResponse(FormStatus status) {
        return new FormSimpleResponse(
                1L,
                "김밤돌",
                status,
                FormType.REGULAR
        );
    }

    public static FormResponse createFormResponse() {
        return new FormResponse(
                1L,
                new ApplicantResponse(
                        "https://maru.com/photo.png",
                        "김밤돌",
                        "01012345678",
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new ParentResponse(
                        "김이로",
                        "01012345678",
                        "18071",
                        "부산광역시 가락대로1393",
                        "부산소프트웨어마이스터고"
                ),
                new EducationResponse(
                        GraduationType.EXPECTED,
                        "2021",
                        "비전중학교",
                        "경기도",
                        "7631003",
                        "나교사",
                        "0519701234"
                ),
                new GradeResponse(
                        List.of(
                                new SubjectResponse(2, 1, "국어", AchievementLevel.A),
                                new SubjectResponse(2, 1, "수학", AchievementLevel.A),
                                new SubjectResponse(2, 1, "사회", AchievementLevel.A),
                                new SubjectResponse(2, 1, "과학", AchievementLevel.A),
                                new SubjectResponse(2, 1, "영어", AchievementLevel.A),
                                new SubjectResponse(2, 1, "체육", AchievementLevel.A),
                                new SubjectResponse(2, 2, "국어", AchievementLevel.A),
                                new SubjectResponse(2, 2, "수학", AchievementLevel.A),
                                new SubjectResponse(2, 2, "사회", AchievementLevel.A),
                                new SubjectResponse(2, 2, "과학", AchievementLevel.A),
                                new SubjectResponse(2, 2, "영어", AchievementLevel.A),
                                new SubjectResponse(2, 2, "체육", AchievementLevel.A),
                                new SubjectResponse(3, 1, "국어", AchievementLevel.A),
                                new SubjectResponse(3, 1, "수학", AchievementLevel.B),
                                new SubjectResponse(3, 1, "사회", AchievementLevel.A),
                                new SubjectResponse(3, 1, "과학", AchievementLevel.A),
                                new SubjectResponse(3, 1, "영어", AchievementLevel.A),
                                new SubjectResponse(3, 1, "체육", AchievementLevel.A)
                        ),
                        new AttendanceResponse(0, 0, 0, 2),
                        new AttendanceResponse(2, 1, 0, 0),
                        new AttendanceResponse(0, 0, 1, 0),
                        8,
                        2,
                        1,
                        List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2)
                ),
                new DocumentResponse(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                "https://maru.bamdoliro.com/form.pdf",
                FormType.REGULAR,
                FormStatus.SUBMITTED
        );
    }

    private static ApplicantRequest createApplicantRequest() {
        return new ApplicantRequest(
                "https://maru.com/photo.png",
                "김밤돌",
                "01012345678",
                LocalDate.of(2005, 4, 15),
                Gender.FEMALE
        );
    }

    private static ParentRequest createParentRequest() {
        return new ParentRequest(
                "김이로",
                "01012345678",
                "18071",
                "부산광역시 가락대로1393",
                "부산소프트웨어마이스터고"
        );
    }

    private static DocumentRequest createDocumentRequest() {
        return new DocumentRequest(
                "하이난김밤돌",
                "공부열심히할게용"
        );
    }
}
