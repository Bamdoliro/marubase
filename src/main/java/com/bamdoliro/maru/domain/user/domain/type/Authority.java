package com.bamdoliro.maru.domain.user.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority implements EnumProperty {
    ROLE_USER("일반 사용자"),
    ROLE_ADMIN("어드민");

    private final String description;
}
