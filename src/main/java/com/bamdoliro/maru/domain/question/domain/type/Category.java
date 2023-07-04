package com.bamdoliro.maru.domain.question.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category implements EnumProperty {
    Top_Question("질문 top"),
    Admission_Process("입학 과정"),
    School_Life("학교 생활"),
    Submit_Documents("관련 제출 서류");


    private final String description;
}
