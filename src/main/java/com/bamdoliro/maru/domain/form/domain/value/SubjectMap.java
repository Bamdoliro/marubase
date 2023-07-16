package com.bamdoliro.maru.domain.form.domain.value;

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
public class SubjectMap {

    private HashMap<String, List<Subject>> value;

    public SubjectMap(List<Subject> subjectList) {
        this.value = subjectList.stream()
                .collect(Collectors.groupingBy(Subject::toString,
                        HashMap::new,
                        Collectors.toList()
                ));
    }

    public Double getScoreOf(int grade, int semester) {
        String key = String.format("%d%d", grade, semester);
        return SubjectList.of(value.get(key)).getAverageScore();
    }

    public SubjectList getSubjectListOf(int grade, int semester) {
        String key = String.format("%d%d", grade, semester);
        return SubjectList.of(value.get(key));
    }
}
