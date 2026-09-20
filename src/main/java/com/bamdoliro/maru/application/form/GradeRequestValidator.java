package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.exception.InvalidGradeException;
import com.bamdoliro.maru.presentation.form.dto.request.EducationRequest;
import com.bamdoliro.maru.presentation.form.dto.request.GradeRequest;

import java.util.List;

/**
 * 임시저장(POST /form/draft)은 검증을 타지 않고 Redis에 그대로 저장되므로,
 * 성적이 비어 있는 초안이 제출/수정 요청으로 그대로 들어올 수 있다.
 * 그 상태로 점수를 계산하면 0/0 = NaN이 되어 정체불명의 500으로 이어지므로
 * 제출/수정 시점에 학기별 성적 존재 여부를 먼저 검증한다.
 */
final class GradeRequestValidator {

    private GradeRequestValidator() {
    }

    static void validate(EducationRequest education, GradeRequest grade) {
        List<Subject> subjectList = toSubjectList(grade);

        if (education.getGraduationType() == GraduationType.QUALIFICATION_EXAMINATION) {
            if (subjectList.stream().noneMatch(subject -> subject.getCount() > 0)) {
                throw new InvalidGradeException("검정고시");
            }
            return;
        }

        validateSemesterHasSubject(subjectList, 2, 1, "2학년 1학기");
        validateSemesterHasSubject(subjectList, 2, 2, "2학년 2학기");
        validateSemesterHasSubject(subjectList, 3, 1, "3학년 1학기");
    }

    private static List<Subject> toSubjectList(GradeRequest grade) {
        if (grade.getSubjectList() == null) {
            return List.of();
        }

        return grade.getSubjectList().stream()
                .flatMap(subjectRequest -> subjectRequest.toValue().stream())
                .toList();
    }

    private static void validateSemesterHasSubject(List<Subject> subjectList, int gradeNumber, int semester, String label) {
        boolean hasSubject = subjectList.stream()
                .anyMatch(subject ->
                        gradeNumber == subject.getGrade() &&
                        semester == subject.getSemester() &&
                        subject.getCount() > 0
                );

        if (!hasSubject) {
            throw new InvalidGradeException(label);
        }
    }
}
