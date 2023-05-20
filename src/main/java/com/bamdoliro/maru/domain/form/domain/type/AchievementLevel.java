package com.bamdoliro.maru.domain.form.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementLevel implements EnumProperty {
    A("A", 5),
    B("B", 4),
    C("C", 3),
    D("D", 2),
    F("E", 1);

    private final String description;
    private final int score;
}
