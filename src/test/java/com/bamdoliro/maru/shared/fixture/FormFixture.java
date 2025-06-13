package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.*;
import com.bamdoliro.maru.domain.form.domain.value.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;
import com.bamdoliro.maru.presentation.form.dto.request.*;
import com.bamdoliro.maru.presentation.form.dto.response.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bamdoliro.maru.shared.util.RandomUtil.randomDouble;
import static com.bamdoliro.maru.shared.util.RandomUtil.randomNumber;

public class FormFixture {

    public static Form createForm(FormType type) {
        Form form = new Form(
                new Applicant(
                        "김밤돌",
                        new PhoneNumber("01012345678"),
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        new PhoneNumber("01012345678"),
                        "엄마",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.EXPECTED,
                        "2021",
                        new School("비전중학교", "경기도", "경기도 비전시 비전구 비전로 1", "7631003"),
                        new Teacher("나교사", new PhoneNumber("0519701234"), new PhoneNumber("01012344321"))
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
                        new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2))
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                type,
                UserFixture.createUser()
        );
        form.updateScore(new Score(1.0, 0.5, 2, 3, 4));
        return form;
    }

    public static Form createMaleForm(FormType type) {
        Form form = new Form(
                new Applicant(
                        "김밤돌",
                        new PhoneNumber("01012345678"),
                        LocalDate.of(2005, 4, 15),
                        Gender.MALE
                ),
                new Parent(
                        "김이로",
                        new PhoneNumber("01012345678"),
                        "엄마",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.EXPECTED,
                        "2021",
                        new School("비전중학교", "경기도", "경기도 비전시 비전구 비전로 1", "7631003"),
                        new Teacher("나교사", new PhoneNumber("0519701234"), new PhoneNumber("01012344321"))
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
                        new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2))
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                type,
                UserFixture.createUser()
        );
        form.updateScore(new Score(1.0, 0.5, 2, 3, 4));
        return form;
    }

    public static Form createRandomBusanForm(User user) {
        return new Form(
                new Applicant(
                        "김밤돌",
                        new PhoneNumber("01085852525"),
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        new PhoneNumber("01085852626"),
                        "엄마",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.EXPECTED,
                        "2021",
                        new School("부산중학교", "부산광역시", "부산광역시 동구 초량로40번길 29", "7631003"),
                        new Teacher("나교사", new PhoneNumber("0519701234"), new PhoneNumber("01012344321"))
                ),
                new Grade(
                        new SubjectList(
                                List.of(
                                        new Subject(2, 1, "국어", randomAchievementLevel()),
                                        new Subject(2, 1, "도덕", randomAchievementLevel()),
                                        new Subject(2, 1, "역사", randomAchievementLevel()),
                                        new Subject(2, 1, "수학", randomAchievementLevel()),
                                        new Subject(2, 1, "과학", randomAchievementLevel()),
                                        new Subject(2, 1, "영어", randomAchievementLevel()),
                                        new Subject(2, 1, "체육", random3AchievementLevel()),
                                        new Subject(2, 1, "미술", random3AchievementLevel()),
                                        new Subject(2, 1, "음악", random3AchievementLevel()),

                                        new Subject(2, 2, "국어", randomAchievementLevel()),
                                        new Subject(2, 2, "도덕", randomAchievementLevel()),
                                        new Subject(2, 2, "역사", randomAchievementLevel()),
                                        new Subject(2, 2, "수학", randomAchievementLevel()),
                                        new Subject(2, 2, "과학", randomAchievementLevel()),
                                        new Subject(2, 2, "영어", randomAchievementLevel()),
                                        new Subject(2, 2, "체육", random3AchievementLevel()),
                                        new Subject(2, 2, "미술", random3AchievementLevel()),
                                        new Subject(2, 2, "음악", random3AchievementLevel()),
                                        new Subject(2, 2, "한문", randomAchievementLevel()),

                                        new Subject(3, 1, "국어", randomAchievementLevel()),
                                        new Subject(3, 1, "사회", randomAchievementLevel()),
                                        new Subject(3, 1, "역사", randomAchievementLevel()),
                                        new Subject(3, 1, "수학", randomAchievementLevel()),
                                        new Subject(3, 1, "과학", randomAchievementLevel()),
                                        new Subject(3, 1, "기가", randomAchievementLevel()),
                                        new Subject(3, 1, "영어", randomAchievementLevel()),
                                        new Subject(3, 1, "체육", random3AchievementLevel()),
                                        new Subject(3, 1, "미술", random3AchievementLevel()),
                                        new Subject(3, 1, "음악", random3AchievementLevel()),
                                        new Subject(3, 1, "한문", randomAchievementLevel())
                                )),
                        new Attendance(randomNumber(0, 5), randomNumber(0, 5), randomNumber(0, 5), randomNumber(0, 5)),
                        new Attendance(randomNumber(0, 3), randomNumber(0, 3), randomNumber(0, 3), randomNumber(0, 3)),
                        new Attendance(randomNumber(0, 2), randomNumber(0, 2), randomNumber(0, 2), randomNumber(0, 2)),
                        randomNumber(0, 100),
                        randomNumber(0, 100),
                        randomNumber(0, 100),
                        new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2, Certificate.CRAFTSMAN_COMPUTER))
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                randomFormType(),
                user
        );
    }

    public static Form createRandomOtherRegionForm(User user) {
        return new Form(
                new Applicant(
                        "김밤돌",
                        new PhoneNumber("01085852525"),
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        new PhoneNumber("01085852626"),
                        "엄마",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.EXPECTED,
                        "2021",
                        new School("비전중학교", "경기도","경기도 비전시 비전구 비전로 1", "7631003"),
        new Teacher("나교사", new PhoneNumber("0519701234"), new PhoneNumber("01012344321"))
                ),
        new Grade(
                new SubjectList(
                        List.of(
                                new Subject(2, 1, "국어", randomAchievementLevel()),
                                new Subject(2, 1, "도덕", randomAchievementLevel()),
                                new Subject(2, 1, "역사", randomAchievementLevel()),
                                new Subject(2, 1, "수학", randomAchievementLevel()),
                                new Subject(2, 1, "과학", randomAchievementLevel()),
                                new Subject(2, 1, "영어", randomAchievementLevel()),
                                new Subject(2, 1, "체육", random3AchievementLevel()),
                                new Subject(2, 1, "미술", random3AchievementLevel()),
                                new Subject(2, 1, "음악", random3AchievementLevel()),

                                new Subject(2, 2, "국어", randomAchievementLevel()),
                                new Subject(2, 2, "도덕", randomAchievementLevel()),
                                new Subject(2, 2, "역사", randomAchievementLevel()),
                                new Subject(2, 2, "수학", randomAchievementLevel()),
                                new Subject(2, 2, "과학", randomAchievementLevel()),
                                new Subject(2, 2, "영어", randomAchievementLevel()),
                                new Subject(2, 2, "체육", random3AchievementLevel()),
                                new Subject(2, 2, "미술", random3AchievementLevel()),
                                new Subject(2, 2, "음악", random3AchievementLevel()),
                                new Subject(2, 2, "한문", randomAchievementLevel()),

                                new Subject(3, 1, "국어", randomAchievementLevel()),
                                new Subject(3, 1, "사회", randomAchievementLevel()),
                                new Subject(3, 1, "역사", randomAchievementLevel()),
                                new Subject(3, 1, "수학", randomAchievementLevel()),
                                new Subject(3, 1, "과학", randomAchievementLevel()),
                                new Subject(3, 1, "기가", randomAchievementLevel()),
                                new Subject(3, 1, "영어", randomAchievementLevel()),
                                new Subject(3, 1, "체육", random3AchievementLevel()),
                                new Subject(3, 1, "미술", random3AchievementLevel()),
                                new Subject(3, 1, "음악", random3AchievementLevel()),
                                new Subject(3, 1, "한문", randomAchievementLevel())
                        )),
                new Attendance(randomNumber(0, 5), randomNumber(0, 5), randomNumber(0, 5), randomNumber(0, 5)),
                new Attendance(randomNumber(0, 3), randomNumber(0, 3), randomNumber(0, 3), randomNumber(0, 3)),
                new Attendance(randomNumber(0, 2), randomNumber(0, 2), randomNumber(0, 2), randomNumber(0, 2)),
                randomNumber(0, 100),
                randomNumber(0, 100),
                randomNumber(0, 100),
                new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2, Certificate.CRAFTSMAN_COMPUTER))
        ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                randomFormType(),
                user
        );
    }

    public static Form createRandomQualificationExaminationForm(User user) {
        return new Form(
                new Applicant(
                        "김밤돌",
                        new PhoneNumber("01012345678"),
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        new PhoneNumber("01012345678"),
                        "엄마",
                        new Address(
                                "18071",
                                "부산광역시 가락대로1393",
                                "부산소프트웨어마이스터고"
                        )
                ),
                new Education(
                        GraduationType.QUALIFICATION_EXAMINATION,
                        "2021",
                        new School(null, null, null, null),
                        new Teacher(null, null, null)
                ),
                new Grade(
                        new SubjectList(
                                List.of(
                                        new Subject("국어", randomNumber(50, 100)),
                                        new Subject("수학", randomNumber(50, 100)),
                                        new Subject("사회", randomNumber(50, 100)),
                                        new Subject("과학", randomNumber(50, 100)),
                                        new Subject("영어", randomNumber(50, 100)),
                                        new Subject("도덕", randomNumber(50, 100))
                                )),
                        new Attendance(randomNumber(0, 5), randomNumber(0, 5), randomNumber(0, 5), randomNumber(0, 5)),
                        new Attendance(randomNumber(0, 3), randomNumber(0, 3), randomNumber(0, 3), randomNumber(0, 3)),
                        new Attendance(randomNumber(0, 2), randomNumber(0, 2), randomNumber(0, 2), randomNumber(0, 2)),
                        randomNumber(0, 100),
                        randomNumber(0, 100),
                        randomNumber(0, 100),
                        new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2, Certificate.CRAFTSMAN_COMPUTER))
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                FormType.REGULAR,
                user
        );
    }


    private static AchievementLevel randomAchievementLevel() {
        AchievementLevel[] values = AchievementLevel.values();
        return values[new Random().nextInt(values.length)];
    }

    private static AchievementLevel random3AchievementLevel() {
        AchievementLevel[] values = {AchievementLevel.A, AchievementLevel.B, AchievementLevel.C};
        return values[new Random().nextInt(values.length)];
    }

    private static FormType randomFormType() {
        FormType[] values = {FormType.REGULAR, FormType.REGULAR, FormType.REGULAR, FormType.REGULAR, FormType.MEISTER_TALENT, FormType.MEISTER_TALENT, FormType.MEISTER_TALENT, FormType.MEISTER_TALENT, FormType.ONE_PARENT, FormType.MULTI_CHILDREN};
        return values[new Random().nextInt(values.length)];
    }

    public static Form createQualificationExaminationForm(FormType type) {
        return new Form(
                new Applicant(
                        "김밤돌",
                        new PhoneNumber("01012345678"),
                        LocalDate.of(2005, 4, 15),
                        Gender.FEMALE
                ),
                new Parent(
                        "김이로",
                        new PhoneNumber("01012345678"),
                        "엄마",
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
                        new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2))
                ),
                new Document(
                        "하이난김밤돌",
                        "공부열심히할게용"
                ),
                type,
                UserFixture.createUser()
        );
    }


    public static SubmitFormRequest createFormRequest(FormType type) {
        return new SubmitFormRequest(
                createApplicantRequest(),
                createParentRequest(),
                new EducationRequest(
                        GraduationType.EXPECTED,
                        "2021",
                        "비전중학교",
                        "경기도",
                        "경기도 비전시 비전구 비전로 1",
                        "7631003",
                        "나교사",
                        "0519701234",
                        "01012344321"
                ),
                new GradeRequest(
                        List.of(new SubjectRequest("국어", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("수학", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("사회", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("과학", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("영어", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("도덕", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("기술가정", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("역사", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("미술", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("음악", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("체육", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("한문", null, randomAchievementLevel(), null, null)
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

    public static SubmitFormRequest createQualificationExaminationFormRequest(FormType type) {
        return new SubmitFormRequest(
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
                        null,
                        null
                ),
                new GradeRequest(
                        List.of(new SubjectRequest("국어", null, null, null, randomNumber(50, 100)),
                                new SubjectRequest("수학", null, null, null, randomNumber(50, 100)),
                                new SubjectRequest("사회", null, null, null, randomNumber(50, 100)),
                                new SubjectRequest("과학", null, null, null, randomNumber(50, 100)),
                                new SubjectRequest("영어", null, null, null, randomNumber(50, 100)),
                                new SubjectRequest("도덕", null, null, null, randomNumber(50, 100))
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
                        "경기도 비전시 비전구 비전로 1",
                        "7631003",
                        "나교사",
                        "0519701234",
                        "01012344321"
                ),
                new GradeRequest(
                        List.of(new SubjectRequest("국어", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("수학", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("사회", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("과학", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("영어", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("체육", randomAchievementLevel(), randomAchievementLevel(), randomAchievementLevel(), null),
                                new SubjectRequest("한문", null, randomAchievementLevel(), null, null)
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
        Form form = FormFixture.createForm(FormType.REGULAR);
        return new FormSimpleResponse(form);
    }

    public static FormResponse createFormResponse() {
        return new FormResponse(
                1L,
                2001L,
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
                        "엄마",
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
                        "0519701234",
                        "경기도 비전시 비전구 비전로 1",
                        "나교사",
                        "01012345678"
                ),
                new GradeResponse(
                        List.of(
                                new SubjectResponse(2, 1, "국어", randomAchievementLevel()),
                                new SubjectResponse(2, 1, "수학", randomAchievementLevel()),
                                new SubjectResponse(2, 1, "사회", randomAchievementLevel()),
                                new SubjectResponse(2, 1, "과학", randomAchievementLevel()),
                                new SubjectResponse(2, 1, "영어", randomAchievementLevel()),
                                new SubjectResponse(2, 1, "체육", randomAchievementLevel()),
                                new SubjectResponse(2, 2, "국어", randomAchievementLevel()),
                                new SubjectResponse(2, 2, "수학", randomAchievementLevel()),
                                new SubjectResponse(2, 2, "사회", randomAchievementLevel()),
                                new SubjectResponse(2, 2, "과학", randomAchievementLevel()),
                                new SubjectResponse(2, 2, "영어", randomAchievementLevel()),
                                new SubjectResponse(2, 2, "체육", randomAchievementLevel()),
                                new SubjectResponse(3, 1, "국어", randomAchievementLevel()),
                                new SubjectResponse(3, 1, "수학", AchievementLevel.B),
                                new SubjectResponse(3, 1, "사회", randomAchievementLevel()),
                                new SubjectResponse(3, 1, "과학", randomAchievementLevel()),
                                new SubjectResponse(3, 1, "영어", randomAchievementLevel()),
                                new SubjectResponse(3, 1, "체육", randomAchievementLevel())
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
                new ScoreResponse(
                        randomDouble(0.0, 240.0),
                        randomDouble(80.0, 400.0)
                ),
                "https://maru.bamdoliro.com/form.pdf",
                FormType.REGULAR,
                FormStatus.FINAL_SUBMITTED,
                false
        );
    }

    public static ApplicantRequest createApplicantRequest() {
        return new ApplicantRequest(
                "김밤돌",
                "01012345678",
                LocalDate.of(2005, 4, 15),
                Gender.FEMALE
        );
    }

    public static ParentRequest createParentRequest() {
        return new ParentRequest(
                "김이로",
                "01012345678",
                "엄마",
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

    public static List<Form> generateBusanFormList(List<User> userList) {
        return userList.stream()
                .map(FormFixture::createRandomBusanForm)
                .collect(Collectors.toList());
    }


    public static List<Form> generateOtherRegionFormList(List<User> userList) {
        return userList.stream()
                .map(FormFixture::createRandomOtherRegionForm)
                .collect(Collectors.toList());

    }
    public static FormUrlVo createFormUrlVo() {
        return new FormUrlVo(
                (long) randomNumber(1000, 5000),
                "김밤돌",
                UUID.randomUUID()
        );
    }

    public static FormUrlResponse createFormUrlResponse() {
        FormUrlVo formUrlVo = createFormUrlVo();
        return new FormUrlResponse(
                formUrlVo.getExaminationNumber(),
                formUrlVo.getName(),
                "https://maru.bamdoliro.com/form.pdf"
        );
    }

    public static AdmissionAndPledgeUrlResponse createAdmissionAndPledgeUrlResponse() {
        FormUrlVo formUrlVo = createFormUrlVo();
        return new AdmissionAndPledgeUrlResponse(
                formUrlVo.getExaminationNumber(),
                formUrlVo.getName(),
                "https://maru.bamdoliro.com/admission-and-pledge.pdf"
        );
    }
}