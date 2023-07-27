package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 15, message = "15자 이하여야 합니다.")
    private String subjectName;

    @Nullable
    private AchievementLevel achievementLevel21;

    @Nullable
    private AchievementLevel achievementLevel22;

    @Nullable
    private AchievementLevel achievementLevel31;

    public List<Subject> toValue() {
        List<Subject> subjectList = new ArrayList<>();

        if (Objects.nonNull(achievementLevel21)) {
            subjectList.add(new Subject(2, 1, subjectName, achievementLevel21));
        }

        if (Objects.nonNull(achievementLevel22)) {
            subjectList.add(new Subject(2, 2, subjectName, achievementLevel22));
        }

        if (Objects.nonNull(achievementLevel31)) {
            subjectList.add(new Subject(3, 1, subjectName, achievementLevel31));
        }

        return subjectList;
    }
}
