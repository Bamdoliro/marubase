package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
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
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;

import java.time.LocalDate;
import java.util.List;

public class FormFixture {

    public static Form createForm(FormType type) {
        return new Form(
                new Applicant(
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
                        "2021"
                ),
                new Grade(
                        new SubjectList(List.of(
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
                        "2021"
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
}
