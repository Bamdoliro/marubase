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
}
