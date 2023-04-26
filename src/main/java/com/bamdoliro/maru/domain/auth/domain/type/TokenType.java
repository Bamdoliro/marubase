package com.bamdoliro.maru.domain.auth.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType implements EnumProperty {
    REFRESH_TOKEN("refresh"),
    ACCESS_TOKEN("access");

    private final String description;
}
