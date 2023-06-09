package com.bamdoliro.maru.domain.form.domain.value;

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

    @Column(nullable = false)
    private Integer attendanceScore;

    @Column(nullable = false)
    private Integer volunteerScore;

    @Column(nullable = false)
    private Integer bonusScore;

    @Column(nullable = true)
    private Integer depthInterviewScore;

    @Column(nullable = true)
    private Integer codingTestScore;

    @Column(nullable = true)
    private Integer ncsScore;

    public Score(Double subjectGradeScore, Integer attendanceScore, Integer volunteerScore, Integer bonusScore) {
        this.subjectGradeScore = subjectGradeScore;
        this.attendanceScore = attendanceScore;
        this.volunteerScore = volunteerScore;
        this.bonusScore = bonusScore;
    }

    public void updateSecondRoundMeisterScore(Integer depthInterviewScore, Integer codingTestScore, Integer ncsScore) {
        this.depthInterviewScore = depthInterviewScore;
        this.codingTestScore = codingTestScore;
        this.ncsScore = ncsScore;
    }

    public void updateSecondRoundScore(Integer depthInterviewScore, Integer ncsScore) {
        this.depthInterviewScore = depthInterviewScore;
        this.ncsScore = ncsScore;
    }
}
