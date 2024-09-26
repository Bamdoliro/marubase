package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.domain.form.domain.value.SubjectMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.bamdoliro.maru.domain.form.constant.FormConstant.DEFAULT_ATTENDANCE_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.DEFAULT_VOLUNTEER_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MAX_ABSENCE_COUNT;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MAX_ATTENDANCE_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MAX_BONUS_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MAX_VOLUNTEER_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MAX_VOLUNTEER_TIME;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MIN_ATTENDANCE_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MIN_VOLUNTEER_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.MIN_VOLUNTEER_TIME;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.REGULAR_TYPE_DEFAULT_SCORE;
import static com.bamdoliro.maru.domain.form.constant.FormConstant.SPECIAL_TYPE_DEFAULT_SCORE;

@RequiredArgsConstructor
@Service
public class CalculateFormScoreService {

    public void execute(Form form) {
        Double subjectGradeScore = calculateSubjectGradeScore(form);
        Integer attendanceScore = calculateAttendanceScore(form);
        Integer volunteerScore = calculateVolunteerScore(form);
        Integer bonusScore = calculateBonusScore(form);

        if (form.getEducation().isQualificationExamination()) {
            form.updateScore(new Score(
                    subjectGradeScore,
                    attendanceScore,
                    volunteerScore,
                    bonusScore
            ));
        } else {
            Double thirdGradeFirstSemesterSubjectGradeScore = form.getGrade().getSubjectList().getSubjectMap().getScoreOf(3, 1);
            form.updateScore(new Score(
                    subjectGradeScore,
                    thirdGradeFirstSemesterSubjectGradeScore,
                    attendanceScore,
                    volunteerScore,
                    bonusScore
            ));
        }
    }

    public Double calculateSubjectGradeScore(Form form) {
        if (form.getType().isRegular() || form.getType().isSupernumerary()) {
            return calculateRegularScore(form);
        } else if (form.getType().isSpecial()) {
            return calculateSpecialScore(form);
        }

        return null;
    }

    private Double calculateRegularScore(Form form) {
        double score;

        if (form.getEducation().isQualificationExamination()) {
            score = 12 * 2 * form.getGrade().getSubjectList().getAverageScore();
        } else {
            SubjectMap subjectMap = form.getGrade().getSubjectList().getSubjectMap();
            score = 4.8 * (subjectMap.getScoreOf(2, 1) + subjectMap.getScoreOf(2, 2)) +
                    7.2 * 2 * subjectMap.getScoreOf(3, 1);
        }

        return REGULAR_TYPE_DEFAULT_SCORE + score;
    }

    public Double calculateDepthInterviewScoreToRegular(Form form) {
        return (form.getScore().getDepthInterviewScore() / 200) * 120;
    }

    private Double calculateSpecialScore(Form form) {
        double score;

        if (form.getEducation().isQualificationExamination()) {
            score = 7.2 * 2 * form.getGrade().getSubjectList().getAverageScore();
        } else {
            SubjectMap subjectMap = form.getGrade().getSubjectList().getSubjectMap();
            score = 2.88 * (subjectMap.getScoreOf(2, 1) + subjectMap.getScoreOf(2, 2)) +
                    4.32 * 2 * subjectMap.getScoreOf(3, 1);
        }

        return SPECIAL_TYPE_DEFAULT_SCORE + score;
    }

    private Integer calculateAttendanceScore(Form form) {
        if (form.getEducation().isQualificationExamination()) {
            return DEFAULT_ATTENDANCE_SCORE;
        }

        Attendance totalAttendance = form.getGrade().getTotalAttendance();
        int convertedAbsenceCount = getConvertedAbsenceCount(totalAttendance);
        return convertedAbsenceCount > MAX_ABSENCE_COUNT ? MIN_ATTENDANCE_SCORE : MAX_ATTENDANCE_SCORE - convertedAbsenceCount;
    }

    private Integer getConvertedAbsenceCount(Attendance attendance) {
        return attendance.getAbsenceCount() + ((attendance.getLatenessCount() + attendance.getEarlyLeaveCount() + attendance.getClassAbsenceCount()) / 3);
    }

    private Integer calculateVolunteerScore(Form form) {
        if (
                form.getEducation().isQualificationExamination() ||
                        form.getGrade().getVolunteerTime1() == null ||
                        form.getGrade().getVolunteerTime2() == null ||
                        form.getGrade().getVolunteerTime3() == null
        ) {
            return DEFAULT_VOLUNTEER_SCORE;
        }

        int totalVolunteerTime = form.getGrade().getTotalVolunteerTime();

        if (totalVolunteerTime < MIN_VOLUNTEER_TIME) {
            return MIN_VOLUNTEER_SCORE;
        }

        if (totalVolunteerTime > MAX_VOLUNTEER_TIME) {
            return MAX_VOLUNTEER_SCORE;
        }

        return Math.toIntExact(Math.round(MAX_VOLUNTEER_SCORE - ((MAX_VOLUNTEER_TIME - totalVolunteerTime) * 0.5)));
    }

    private Integer calculateBonusScore(Form form) {
        if (Objects.isNull(form.getGrade().getCertificateListValue())) {
            return 0;
        }

        int bonusScore = form.getGrade().getCertificateListValue().stream()
                .mapToInt(Certificate::getScore)
                .sum();

        return Math.min(bonusScore, MAX_BONUS_SCORE);
    }
}
