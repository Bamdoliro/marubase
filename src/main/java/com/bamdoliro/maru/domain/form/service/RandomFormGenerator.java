package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.type.Gender;
import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.form.domain.value.Address;
import com.bamdoliro.maru.domain.form.domain.value.Applicant;
import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import com.bamdoliro.maru.domain.form.domain.value.CertificateList;
import com.bamdoliro.maru.domain.form.domain.value.Document;
import com.bamdoliro.maru.domain.form.domain.value.Education;
import com.bamdoliro.maru.domain.form.domain.value.Grade;
import com.bamdoliro.maru.domain.form.domain.value.Parent;
import com.bamdoliro.maru.domain.form.domain.value.PhoneNumber;
import com.bamdoliro.maru.domain.form.domain.value.School;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;
import com.bamdoliro.maru.domain.form.domain.value.Teacher;
import com.bamdoliro.maru.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 원서 접수 테스트용 랜덤 원서를 만든다. 실제 지원자 데이터에는 사용하지 않는다.
 */
@RequiredArgsConstructor
@Component
public class RandomFormGenerator {

    private static final String[] LAST_NAMES = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
    private static final String[] FIRST_NAMES = {"민준", "서연", "도윤", "하은", "시우", "지유", "예준", "서윤", "주원", "하린", "지호", "수아"};
    private static final AchievementLevel[] THREE_STEP_LEVELS = {AchievementLevel.A, AchievementLevel.B, AchievementLevel.C};
    private static final List<School> SCHOOLS = List.of(
            new School("부산중학교", "부산광역시", "부산광역시 동구 초량로40번길 29", "7631003"),
            new School("해운대중학교", "부산광역시", "부산광역시 해운대구 해운대로 1", "7631004"),
            new School("강서중학교", "부산광역시", "부산광역시 강서구 가락대로 1", "7631005"),
            new School("비전중학교", "경기도", "경기도 비전시 비전구 비전로 1", "7531003")
    );
    private static final List<List<Certificate>> CERTIFICATE_CASES = List.of(
            List.of(),
            List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2),
            List.of(Certificate.CRAFTSMAN_PROGRAMMING),
            List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_2, Certificate.CRAFTSMAN_EMBEDDED)
    );

    private final Clock clock;
    private final Random random = new SecureRandom();

    public String randomPhoneNumber() {
        return "010" + String.format("%08d", random.nextInt(100_000_000));
    }

    public String randomName() {
        return pick(LAST_NAMES) + pick(FIRST_NAMES);
    }

    public Form generate(User user, FormType type) {
        int year = LocalDate.now(clock).getYear();

        return Form.builder()
                .applicant(new Applicant(
                        user.getName(),
                        new PhoneNumber(user.getPhoneNumber()),
                        LocalDate.of(year - 15, random.nextInt(12) + 1, random.nextInt(28) + 1),
                        random.nextBoolean() ? Gender.MALE : Gender.FEMALE
                ))
                .parent(new Parent(
                        randomName(),
                        new PhoneNumber(randomPhoneNumber()),
                        random.nextBoolean() ? "부" : "모",
                        new Address("46708", "부산광역시 강서구 가락대로 1393", "101동 101호")
                ))
                .education(new Education(
                        GraduationType.EXPECTED,
                        String.valueOf(year + 1),
                        SCHOOLS.get(random.nextInt(SCHOOLS.size())),
                        new Teacher("나교사", new PhoneNumber("0519701234"), new PhoneNumber("01012344321"))
                ))
                .grade(new Grade(
                        new SubjectList(randomSubjectList()),
                        randomAttendance(5),
                        randomAttendance(3),
                        randomAttendance(2),
                        random.nextInt(31),
                        random.nextInt(31),
                        random.nextInt(31),
                        new CertificateList(new ArrayList<>(CERTIFICATE_CASES.get(random.nextInt(CERTIFICATE_CASES.size())))),
                        false
                ))
                .document(new Document(
                        "테스트용 자기소개서입니다. 소프트웨어 개발자가 되기 위해 꾸준히 공부하고 있습니다.",
                        "테스트용 학업계획서입니다. 입학 후 전공 역량을 키우기 위해 노력하겠습니다."
                ))
                .type(type)
                .user(user)
                .build();
    }

    private List<Subject> randomSubjectList() {
        List<Subject> subjects = new ArrayList<>();
        addSemester(subjects, 2, 1, List.of("국어", "도덕", "역사", "수학", "과학", "영어"));
        addSemester(subjects, 2, 2, List.of("국어", "도덕", "역사", "수학", "과학", "영어", "한문"));
        addSemester(subjects, 3, 1, List.of("국어", "사회", "역사", "수학", "과학", "기가", "영어", "한문"));
        return subjects;
    }

    private void addSemester(List<Subject> subjects, int grade, int semester, List<String> fiveStepSubjects) {
        fiveStepSubjects.forEach(name -> subjects.add(new Subject(grade, semester, name, randomFiveStepLevel())));
        List.of("체육", "미술", "음악").forEach(name ->
                subjects.add(new Subject(grade, semester, name, pick(THREE_STEP_LEVELS)))
        );
    }

    // 미이수(F)는 성적 계산에서 예외적인 값이라 테스트 데이터에는 A~E만 사용한다.
    private AchievementLevel randomFiveStepLevel() {
        return AchievementLevel.values()[random.nextInt(5)];
    }

    private Attendance randomAttendance(int max) {
        return new Attendance(
                random.nextInt(max + 1),
                random.nextInt(max + 1),
                random.nextInt(max + 1),
                random.nextInt(max + 1)
        );
    }

    private <T> T pick(T[] values) {
        return values[random.nextInt(values.length)];
    }
}
