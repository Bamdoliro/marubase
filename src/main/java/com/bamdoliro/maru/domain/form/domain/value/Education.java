package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Education {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private GraduationType graduationType;

    @Column(nullable = false, length = 4)
    private String graduationYear;

    @Embedded
    private School school;

    @Embedded
    private Teacher teacher;

    public boolean isQualificationExamination() {
        return graduationType.equals(GraduationType.QUALIFICATION_EXAMINATION);
    }
}
