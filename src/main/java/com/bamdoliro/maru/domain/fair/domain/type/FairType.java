package com.bamdoliro.maru.domain.fair.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FairType implements EnumProperty {

    STUDENT_AND_PARENT("학생 및 학부모"),
    TEACHER("교사");

    private final String description;
}
