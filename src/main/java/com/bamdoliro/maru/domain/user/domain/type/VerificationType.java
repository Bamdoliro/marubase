package com.bamdoliro.maru.domain.user.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationType implements EnumProperty {
    SIGNUP("회원가입"),
    UPDATE_PASSWORD("비밀번호 변경");

    private final String description;
}
