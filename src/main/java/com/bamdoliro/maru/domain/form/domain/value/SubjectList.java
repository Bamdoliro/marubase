package com.bamdoliro.maru.domain.form.domain.value;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        return new SubjectList(subjectList);
    }

    public HashMap<String, List<Subject>> getSubjectMap() {
        return value.stream()
                .collect(Collectors.groupingBy(Subject::toString,
                        HashMap::new,
                        Collectors.toList()
                ));
    }

    public Double getAverageScore() {
        return (double) totalScore() / (double) size();
    }

    private Integer totalScore() {
        return value.stream()
                .mapToInt(Subject::getScore)
                .sum();
    }

    private Integer size() {
        return value.stream()
                .mapToInt(Subject::getCount)
                .sum();
    }
}
