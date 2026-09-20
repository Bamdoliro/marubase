package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.form.exception.InvalidGradeException;
import com.bamdoliro.maru.presentation.form.dto.request.EducationRequest;
import com.bamdoliro.maru.presentation.form.dto.request.GradeRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GradeRequestValidatorTest {

    @Test
    void 학기별_성적이_모두_있으면_통과한다() {
        EducationRequest education = createEducationRequest(GraduationType.EXPECTED);
        GradeRequest grade = createGradeRequest(List.of(
                new SubjectRequest("국어", null, null, AchievementLevel.A, AchievementLevel.A, AchievementLevel.A, null)
        ));

        assertDoesNotThrow(() -> GradeRequestValidator.validate(education, grade));
    }

    @Test
    void 과목이_아예_없으면_에러가_발생한다() {
        EducationRequest education = createEducationRequest(GraduationType.EXPECTED);
        GradeRequest grade = createGradeRequest(List.of());

        assertThrows(InvalidGradeException.class, () -> GradeRequestValidator.validate(education, grade));
    }

    @Test
    void 과목_리스트가_null이면_에러가_발생한다() {
        EducationRequest education = createEducationRequest(GraduationType.EXPECTED);
        GradeRequest grade = createGradeRequest(null);

        assertThrows(InvalidGradeException.class, () -> GradeRequestValidator.validate(education, grade));
    }

    @Test
    void 특정_학기_성적만_비어있어도_에러가_발생한다() {
        EducationRequest education = createEducationRequest(GraduationType.EXPECTED);
        // 2학년 1학기(achievementLevel21)만 비워둔다
        GradeRequest grade = createGradeRequest(List.of(
                new SubjectRequest("국어", null, null, null, AchievementLevel.A, AchievementLevel.A, null)
        ));

        assertThrows(InvalidGradeException.class, () -> GradeRequestValidator.validate(education, grade));
    }

    @Test
    void 검정고시인데_과목이_없으면_에러가_발생한다() {
        EducationRequest education = createEducationRequest(GraduationType.QUALIFICATION_EXAMINATION);
        GradeRequest grade = createGradeRequest(List.of());

        assertThrows(InvalidGradeException.class, () -> GradeRequestValidator.validate(education, grade));
    }

    @Test
    void 검정고시는_과목이_하나라도_있으면_통과한다() {
        EducationRequest education = createEducationRequest(GraduationType.QUALIFICATION_EXAMINATION);
        GradeRequest grade = createGradeRequest(List.of(
                new SubjectRequest("국어", null, null, null, null, null, 90)
        ));

        assertDoesNotThrow(() -> GradeRequestValidator.validate(education, grade));
    }

    private EducationRequest createEducationRequest(GraduationType graduationType) {
        return new EducationRequest(
                graduationType,
                "2021",
                "비전중학교",
                "경기도",
                "경기도 비전시 비전구 비전로 1",
                "7631003",
                "나교사",
                "0519701234",
                "01012344321"
        );
    }

    private GradeRequest createGradeRequest(List<SubjectRequest> subjectList) {
        return new GradeRequest(
                subjectList,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
}
