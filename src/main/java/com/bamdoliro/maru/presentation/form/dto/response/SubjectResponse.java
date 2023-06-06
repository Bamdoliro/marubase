package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubjectResponse {
    
    private Integer grade;
    private Integer semester;
    private String subjectName;
    private AchievementLevel achievementLevel;
    
    public SubjectResponse(Subject subject) {
        this.grade = subject.getGrade();
        this.semester = subject.getSemester();
        this.subjectName = subject.getSubjectName();
        this.achievementLevel = subject.getAchievementLevel();
    }
}
