package com.bamdoliro.maru.domain.fair.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FairStatus implements EnumProperty {
    APPLICATION_NOT_STARTED("신청 시작 전"),
    APPLICATION_IN_PROGRESS("신청 진행 중"),
    APPLICATION_ENDED("신청 종료됨"),
    APPLICATION_EARLY_CLOSED("신청 조기 종료됨"),
    CLOSED("종료됨");

    private final String description;
}
