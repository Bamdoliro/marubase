package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.value.Attendance;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FormService {

    // TODO :: 기본점수 constant로 빼기
    public void calculateScore(Form form) {
        Double subjectGradeScore = calculateSubjectGradeScore(form);
        Integer attendanceScore = calculateAttendanceScore(form);
        Integer volunteerScore = calculateVolunteerScore(form);
        Integer bonusScore = calculateBonusScore(form);

        form.updateScore(new Score(
                subjectGradeScore,
                attendanceScore,
                volunteerScore,
                bonusScore
        ));
    }

    private Double calculateSubjectGradeScore(Form form) {
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
            HashMap<String, List<Subject>> subjectMap = form.getGrade().getSubjectList().getSubjectMap();
            score = 4.8 * (SubjectList.of(subjectMap.get("21")).getAverageScore() + SubjectList.of(subjectMap.get("22")).getAverageScore()) +
                    7.2 * 2 * SubjectList.of(subjectMap.get("31")).getAverageScore();
        }

        return 80 + MathUtil.roundTo(score, 3);
    }

    private Double calculateSpecialScore(Form form) {
        double score;

        if (form.getEducation().isQualificationExamination()) {
            score = 7.2 * 2 * form.getGrade().getSubjectList().getAverageScore();
        } else {
            HashMap<String, List<Subject>> subjectMap = form.getGrade().getSubjectList().getSubjectMap();
            score = 2.88 * (SubjectList.of(subjectMap.get("21")).getAverageScore() + SubjectList.of(subjectMap.get("22")).getAverageScore()) +
                    4.32 * 2 * SubjectList.of(subjectMap.get("31")).getAverageScore();
        }

        return 48 + MathUtil.roundTo(score, 3);
    }

    private Integer calculateAttendanceScore(Form form) {
        if (form.getEducation().isQualificationExamination()) {
            return 14;
        }

        Attendance totalAttendance = form.getGrade().getTotalAttendance();
        int convertedAbsenceCount = getConvertedAbsenceCount(totalAttendance);
        return convertedAbsenceCount > 18 ? 0 : 18 - convertedAbsenceCount;
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
            return 14;
        }

        int totalVolunteerTime = form.getGrade().getTotalVolunteerTime();

        if (totalVolunteerTime < 7) {
            return 0;
        }

        if (totalVolunteerTime > 13) {
            return 18;
        }

        return Math.toIntExact(Math.round(18 - ((15 - totalVolunteerTime) * 0.5)));
    }

    private Integer calculateBonusScore(Form form) {
        int bonusScore = form.getGrade().getCertificateList().stream()
                .mapToInt(Certificate::getScore)
                .sum();

        return Math.min(bonusScore, 4);
    }
}
