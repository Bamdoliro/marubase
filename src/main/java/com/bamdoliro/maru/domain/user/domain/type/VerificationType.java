package com.bamdoliro.maru.domain.user.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VerificationType {
    SIGNUP("회원가입"),
    UPDATE_PASSWORD("비밀번호 변경");

    private final String description;
}
