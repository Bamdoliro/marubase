package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.shared.util.MathUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Score {

    @Column(nullable = false)
    private Double subjectGradeScore;

    @Column(nullable = true)
    private Double thirdGradeFirstSemesterSubjectGradeScore;

    @Column(nullable = false)
    private Integer attendanceScore;

    @Column(nullable = false)
    private Integer volunteerScore;

    @Column(nullable = false)
    private Integer bonusScore;

    @Column(nullable = true)
    private Double depthInterviewScore;

    @Column(nullable = true)
    private Double codingTestScore;

    @Column(nullable = true)
    private Double ncsScore;

    @Column(nullable = false)
    private Double firstRoundScore;

    @Column(nullable = true)
    private Double totalScore;

    public Score(Double subjectGradeScore, Double thirdGradeFirstSemesterSubjectGradeScore, Integer attendanceScore, Integer volunteerScore, Integer bonusScore) {
        this.subjectGradeScore = MathUtil.roundTo(subjectGradeScore, 3);
        this.thirdGradeFirstSemesterSubjectGradeScore = MathUtil.roundTo(thirdGradeFirstSemesterSubjectGradeScore, 3);
        this.attendanceScore = attendanceScore;
        this.volunteerScore = volunteerScore;
        this.bonusScore = bonusScore;
        this.firstRoundScore = MathUtil.roundTo(subjectGradeScore + attendanceScore + volunteerScore + bonusScore, 3);
    }

    public Score(Double subjectGradeScore, Integer attendanceScore, Integer volunteerScore, Integer bonusScore) {
        this.subjectGradeScore = MathUtil.roundTo(subjectGradeScore, 3);
        this.attendanceScore = attendanceScore;
        this.volunteerScore = volunteerScore;
        this.bonusScore = bonusScore;
        this.firstRoundScore = MathUtil.roundTo(subjectGradeScore + attendanceScore + volunteerScore + bonusScore, 3);
    }

    public void updateSubjectScore(Double subjectGradeScore) {
        this.subjectGradeScore = subjectGradeScore;
        this.firstRoundScore = subjectGradeScore + attendanceScore + volunteerScore + bonusScore;
    }

    public void updateSecondRoundMeisterScore(Double depthInterviewScore, Double ncsScore, Double codingTestScore) {
        this.depthInterviewScore = depthInterviewScore;
        this.ncsScore = ncsScore;
        this.codingTestScore = codingTestScore;
        this.totalScore = firstRoundScore + depthInterviewScore + codingTestScore + ncsScore;
    }

    public void updateSecondRoundScore(Double depthInterviewScore, Double ncsScore) {
        this.depthInterviewScore = depthInterviewScore;
        this.ncsScore = ncsScore;
        this.totalScore = firstRoundScore + depthInterviewScore + ncsScore;
    }

    public void updateSecondRoundMeisterScoreToRegular() {
        this.codingTestScore = null;
        this.totalScore = firstRoundScore + depthInterviewScore + ncsScore;
    }

    public void updateSecondRoundSocialScoreToRegular(Double depthInterviewScore) {
        this.depthInterviewScore = depthInterviewScore;
        this.totalScore = firstRoundScore + depthInterviewScore + ncsScore;
    }
}
