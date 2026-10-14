package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import com.bamdoliro.maru.domain.form.domain.value.CertificateList;
import com.bamdoliro.maru.domain.form.domain.value.Grade;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CalculateFormScoreServiceTest {

    @InjectMocks
    private CalculateFormScoreService calculateFormScoreService;

    @Test
    void 일반전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(195.886, form.getScore().getSubjectGradeScore());
        assertEquals(4.714, form.getScore().getThirdGradeFirstSemesterSubjectGradeScore());
        assertEquals(15, form.getScore().getAttendanceScore());
        assertEquals(0, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 특별전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createForm(FormType.MEISTER_TALENT);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(117.531, form.getScore().getSubjectGradeScore());
        assertEquals(4.714, form.getScore().getThirdGradeFirstSemesterSubjectGradeScore());
        assertEquals(15, form.getScore().getAttendanceScore());
        assertEquals(0, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 검정고시_일반전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createQualificationExaminationForm(FormType.REGULAR);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(155.000, form.getScore().getSubjectGradeScore());
        assertNull(form.getScore().getThirdGradeFirstSemesterSubjectGradeScore());
        assertEquals(14, form.getScore().getAttendanceScore());
        assertEquals(14, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 검정고시_특별전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createQualificationExaminationForm(FormType.MEISTER_TALENT);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(92.6, form.getScore().getSubjectGradeScore());
        assertEquals(14, form.getScore().getAttendanceScore());
        assertEquals(14, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 환산_결석일수가_15일이면_출석성적은_3점이다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceAttendance(form, new Attendance(15, 0, 0, 0));

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(3, form.getScore().getAttendanceScore());
    }

    @Test
    void 환산_결석일수가_16일이면_출석성적은_0점이다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceAttendance(form, new Attendance(16, 0, 0, 0));

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(0, form.getScore().getAttendanceScore());
    }

    @Test
    void 환산_결석일수가_16일을_넘으면_출석성적은_0점이다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceAttendance(form, new Attendance(17, 0, 0, 0));

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(0, form.getScore().getAttendanceScore());
    }

    @Test
    void 미인정_지각_조퇴_결과_3회는_결석_1회로_환산하고_소수점은_버린다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceAttendance(form, new Attendance(1, 2, 2, 1));

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(16, form.getScore().getAttendanceScore());
    }

    @Test
    void 지각_조퇴_결과를_환산해_16일이_되면_출석성적은_0점이다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceAttendance(form, new Attendance(15, 3, 0, 0));

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(0, form.getScore().getAttendanceScore());
    }

    @Test
    void 멘토링_프로그램에_참여하면_가산점이_추가된다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceGrade(form, new CertificateList(List.of()), true);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(1, form.getScore().getBonusScore());
    }

    @Test
    void 가산점_합계가_상한을_초과하면_상한으로_제한된다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        replaceGrade(form, new CertificateList(List.of(Certificate.COMPUTER_SPECIALIST_LEVEL_1, Certificate.CRAFTSMAN_EMBEDDED)), true);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(4, form.getScore().getBonusScore());
    }

    @Test
    void 정보_교과_등급에_따라_일반전형_산출식의_가중치가_달라진다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        List<Subject> subjectList = form.getGrade().getSubjectList().getValue().stream()
                .filter(subject -> !subject.getSubjectName().equals("정보"))
                .collect(Collectors.toCollection(ArrayList::new));
        subjectList.add(new Subject(1, 1, "정보", AchievementLevel.E));
        replaceSubjectList(form, subjectList);

        // when
        calculateFormScoreService.execute(form);

        // then
        assertEquals(193.886, form.getScore().getSubjectGradeScore());
    }

    private void replaceGrade(Form form, CertificateList certificateList, boolean mentoringProgram) {
        Grade grade = form.getGrade();
        Grade newGrade = new Grade(
                grade.getSubjectList(),
                grade.getAttendance1(),
                grade.getAttendance2(),
                grade.getAttendance3(),
                grade.getVolunteerTime1(),
                grade.getVolunteerTime2(),
                grade.getVolunteerTime3(),
                certificateList,
                mentoringProgram
        );
        form.update(form.getApplicant(), form.getParent(), form.getEducation(), newGrade, form.getDocument(), form.getType());
    }

    private void replaceSubjectList(Form form, List<Subject> subjectList) {
        Grade grade = form.getGrade();
        Grade newGrade = new Grade(
                new SubjectList(subjectList),
                grade.getAttendance1(),
                grade.getAttendance2(),
                grade.getAttendance3(),
                grade.getVolunteerTime1(),
                grade.getVolunteerTime2(),
                grade.getVolunteerTime3(),
                grade.getCertificateList(),
                grade.isMentoringProgram()
        );
        form.update(form.getApplicant(), form.getParent(), form.getEducation(), newGrade, form.getDocument(), form.getType());
    }

    private void replaceAttendance(Form form, Attendance attendance1) {
        Grade grade = form.getGrade();
        Attendance zero = new Attendance(0, 0, 0, 0);
        Grade newGrade = new Grade(
                grade.getSubjectList(),
                attendance1,
                zero,
                zero,
                grade.getVolunteerTime1(),
                grade.getVolunteerTime2(),
                grade.getVolunteerTime3(),
                grade.getCertificateList(),
                grade.isMentoringProgram()
        );
        form.update(form.getApplicant(), form.getParent(), form.getEducation(), newGrade, form.getDocument(), form.getType());
    }
}