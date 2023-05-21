package com.bamdoliro.maru.domain.form.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements EnumProperty {
    MALE("남자"),
    FEMALE("여자");

    private final String description;
}
