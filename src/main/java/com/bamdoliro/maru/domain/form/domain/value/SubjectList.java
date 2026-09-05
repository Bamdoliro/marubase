package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class SubjectList {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_subject",
            joinColumns = @JoinColumn(name = "form_id"))
    private List<Subject> value;

    public static SubjectList of(List<Subject> subjectList) {
        return new SubjectList(subjectList == null ? List.of() : subjectList);
    }

    public SubjectMap getSubjectMap() {
        return new SubjectMap(value);
    }

    public Double getAverageScore() {
        return (double) totalScore() / (double) size();
    }

    public Integer totalScore() {
        return value.stream()
                .mapToInt(Subject::getScore)
                .sum();
    }

    public Integer size() {
        return value.stream()
                .mapToInt(Subject::getCount)
                .sum();
    }

    public Double getAverageInformationScore() {
        double score = 0;
        int count = 0;

        for(Subject subject : value) {
            if (
                (subject.getGrade() == 1 ||  subject.getGrade() == 2 || subject.getGrade() == 3 && subject.getSemester() == 1) &&
                subject.getSubjectName().equals("정보")
            ) {
                score += subject.getScore();
                count++;
            }
        }

        return count > 0 ? (score / count) : AchievementLevel.C.getScore();
    }
}
