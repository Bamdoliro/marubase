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
    private Integer subjectGradeScore;

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
}
