package com.bamdoliro.maru.domain.form.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GraduationType implements EnumProperty {
    EXPECTED("졸업 예정"),
    GRADUATED("졸업"),
    QUALIFICATION_EXAMINATION("고입 검정");

    private final String description;
}
