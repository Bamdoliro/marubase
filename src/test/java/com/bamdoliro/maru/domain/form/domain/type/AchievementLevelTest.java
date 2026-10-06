package com.bamdoliro.maru.domain.form.domain.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementLevelTest {

    @Test
    void 개인별_성적_일람표의_성취수준을_영문으로_표시한다() {
        assertAll(
                () -> assertEquals("A", AchievementLevel.A.toGradeTableString()),
                () -> assertEquals("B", AchievementLevel.B.toGradeTableString()),
                () -> assertEquals("C", AchievementLevel.C.toGradeTableString()),
                () -> assertEquals("D", AchievementLevel.D.toGradeTableString()),
                () -> assertEquals("E", AchievementLevel.E.toGradeTableString())
        );
    }

    @Test
    void 개인별_성적_일람표에서_F는_미이수로_표시한다() {
        assertEquals("미이수", AchievementLevel.F.toGradeTableString());
    }
}
