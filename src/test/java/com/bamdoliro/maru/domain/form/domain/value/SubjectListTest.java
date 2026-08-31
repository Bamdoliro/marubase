package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectListTest {

    @Test
    void 정보_과목이_없으면_기본_등급의_점수를_반환한다() {
        // given
        SubjectList subjectList = new SubjectList(List.of(
                new Subject(2, 1, "국어", AchievementLevel.A)
        ));

        // when & then
        assertEquals((double) AchievementLevel.C.getScore(), subjectList.getAverageInformationScore());
    }

    @Test
    void 학년과_학기에_걸친_정보_과목의_평균을_계산한다() {
        // given
        SubjectList subjectList = new SubjectList(List.of(
                new Subject(1, 1, "정보", AchievementLevel.A),
                new Subject(1, 2, "정보", AchievementLevel.B),
                new Subject(2, 1, "정보", AchievementLevel.C),
                new Subject(2, 2, "정보", AchievementLevel.D),
                new Subject(3, 1, "정보", AchievementLevel.A)
        ));

        // when
        Double averageInformationScore = subjectList.getAverageInformationScore();

        // then
        assertEquals(3.8, averageInformationScore);
    }

    @Test
    void 정보가_아닌_과목은_평균에서_제외된다() {
        // given
        SubjectList subjectList = new SubjectList(List.of(
                new Subject(2, 1, "정보", AchievementLevel.A),
                new Subject(2, 1, "국어", AchievementLevel.E)
        ));

        // when
        Double averageInformationScore = subjectList.getAverageInformationScore();

        // then
        assertEquals((double) AchievementLevel.A.getScore(), averageInformationScore);
    }
}
