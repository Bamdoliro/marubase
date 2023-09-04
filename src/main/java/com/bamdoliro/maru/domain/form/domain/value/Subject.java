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

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, length = 15)
    private String subjectName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private AchievementLevel achievementLevel;

    public Integer getScore() {
        if (subjectName.equals("수학")) {
            return achievementLevel.getScore() * 2;
        }

        return achievementLevel.getScore();
    }

    public Integer getCount() {
        if (subjectName.equals("수학")) {
            return 2;
        }

        return 1;
    }

    @Override
    public String toString() {
        return String.format("%d%d", grade, semester);
    }

    public String achievementLevelToString() {
        if (subjectName.equals("음악") ||
                subjectName.equals("미술") ||
                subjectName.equals("체육")
        ) {
            return String.format("%s/%s", achievementLevel.name(), achievementLevel.getKorean3step());
        }

        return String.format("%s/%s", achievementLevel.name(), achievementLevel.getKorean());
    }
}
