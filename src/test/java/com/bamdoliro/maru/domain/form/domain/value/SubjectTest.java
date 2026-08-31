package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectTest {

    @Test
    void 검정고시_원점수_99점_이상은_A등급이다() {
        assertEquals(AchievementLevel.A, new Subject("국어", 99).getAchievementLevel());
        assertEquals(AchievementLevel.A, new Subject("국어", 100).getAchievementLevel());
    }

    @Test
    void 검정고시_원점수_97점_이상_99점_미만은_B등급이다() {
        assertEquals(AchievementLevel.B, new Subject("국어", 97).getAchievementLevel());
        assertEquals(AchievementLevel.B, new Subject("국어", 98).getAchievementLevel());
    }

    @Test
    void 검정고시_원점수_93점_이상_97점_미만은_C등급이다() {
        assertEquals(AchievementLevel.C, new Subject("국어", 93).getAchievementLevel());
        assertEquals(AchievementLevel.C, new Subject("국어", 96).getAchievementLevel());
    }

    @Test
    void 검정고시_원점수_89점_이상_93점_미만은_D등급이다() {
        assertEquals(AchievementLevel.D, new Subject("국어", 89).getAchievementLevel());
        assertEquals(AchievementLevel.D, new Subject("국어", 92).getAchievementLevel());
    }

    @Test
    void 검정고시_원점수_89점_미만은_E등급이다() {
        assertEquals(AchievementLevel.E, new Subject("국어", 88).getAchievementLevel());
        assertEquals(AchievementLevel.E, new Subject("국어", 0).getAchievementLevel());
    }
}
