package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Subject {

    @Column(nullable = true)
    private Integer grade;

    @Column(nullable = true)
    private Integer semester;

    @Column(nullable = false, length = 15)
    private String subjectName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AchievementLevel achievementLevel;

    @Column(nullable = true, name = "score")
    private Integer originalScore;

    public Subject(Integer grade, Integer semester, String subjectName, AchievementLevel achievementLevel) {
        this.grade = grade;
        this.semester = semester;
        this.subjectName = subjectName;
        this.achievementLevel = achievementLevel;
    }

    public Subject(String subjectName, Integer originalScore) {
        this.grade = 0;
        this.semester = 0;
        this.subjectName = subjectName;
        this.originalScore = originalScore;
        this.achievementLevel = calculateQualificationExaminationAchievementLevel(originalScore);
    }

    private AchievementLevel calculateQualificationExaminationAchievementLevel(int score) {
        if (score >= 99) {
            return AchievementLevel.A;
        } else if (score >= 97) {
            return AchievementLevel.B;
        } else if (score >= 93) {
            return AchievementLevel.C;
        } else if (score >= 89) {
            return AchievementLevel.D;
        }

        return AchievementLevel.E;
    }

    public Integer getScore() {
        if (achievementLevel == AchievementLevel.F) {
            if ("국어".equals(subjectName) || "영어".equals(subjectName) || "수학".equals(subjectName)) {
                return AchievementLevel.C.getScore() * ("수학".equals(subjectName) ? 2 : 1);
            }
            return AchievementLevel.F.getScore();
        }

        if (subjectName.equals("수학")) {
            return achievementLevel.getScore() * 2;
        }

        return achievementLevel.getScore();
    }

    public Integer getCount() {
        if(achievementLevel == AchievementLevel.F) {
            if(!(subjectName.equals("수학") || subjectName.equals("국어") || subjectName.equals("영어"))){
                return 0;
            }
        }

        if (subjectName.equals("수학")) {
            return 2;
        }

        return 1;
    }

    @Override
    public String toString() {
        return String.format("%d%d", grade, semester);
    }
}
