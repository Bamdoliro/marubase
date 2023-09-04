package com.bamdoliro.maru.domain.form.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementLevel implements EnumProperty {
    A("A", 5, "수", "우수"),
    B("B", 4, "우", "보통"),
    C("C", 3, "미", "미흡"),
    D("D", 2, "양", null),
    E("E", 1, "가", null);

    private final String description;
    private final int score;
    private final String korean;
    private final String korean3step;
}
