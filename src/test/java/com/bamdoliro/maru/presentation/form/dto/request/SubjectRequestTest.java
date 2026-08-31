package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubjectRequestTest {

    @Test
    void _1학년_성취도가_존재하면_1학년_과목으로_변환한다() {
        // given
        SubjectRequest request = new SubjectRequest(
                "정보",
                AchievementLevel.A,
                AchievementLevel.B,
                null,
                null,
                null,
                null
        );

        // when
        List<Subject> subjectList = request.toValue();

        // then
        assertEquals(2, subjectList.size());
        assertEquals(1, subjectList.get(0).getGrade());
        assertEquals(1, subjectList.get(0).getSemester());
        assertEquals(AchievementLevel.A, subjectList.get(0).getAchievementLevel());
        assertEquals(1, subjectList.get(1).getGrade());
        assertEquals(2, subjectList.get(1).getSemester());
        assertEquals(AchievementLevel.B, subjectList.get(1).getAchievementLevel());
    }

    @Test
    void _1학년_성취도가_없으면_1학년_과목을_생성하지_않는다() {
        // given
        SubjectRequest request = new SubjectRequest(
                "국어",
                null,
                null,
                AchievementLevel.A,
                AchievementLevel.A,
                AchievementLevel.B,
                null
        );

        // when
        List<Subject> subjectList = request.toValue();

        // then
        assertEquals(3, subjectList.size());
        assertTrue(subjectList.stream().noneMatch(subject -> subject.getGrade() == 1));
    }

    @Test
    void 검정고시_원점수가_있으면_성취도_필드는_무시한다() {
        // given
        SubjectRequest request = new SubjectRequest(
                "정보",
                AchievementLevel.A,
                AchievementLevel.A,
                AchievementLevel.A,
                AchievementLevel.A,
                AchievementLevel.A,
                85
        );

        // when
        List<Subject> subjectList = request.toValue();

        // then
        assertEquals(1, subjectList.size());
        assertEquals("정보", subjectList.get(0).getSubjectName());
        assertEquals(85, subjectList.get(0).getOriginalScore());
    }
}
