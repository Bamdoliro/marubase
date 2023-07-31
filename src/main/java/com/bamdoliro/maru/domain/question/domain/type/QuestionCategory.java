package com.bamdoliro.maru.domain.question.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory implements EnumProperty {
    TOP_QUESTION("질문 TOP"),
    ADMISSION_PROCESS("입학 과정"),
    SCHOOL_LIFE("학교 생활"),
    SUBMIT_DOCUMENT("관련 제출 서류");

    private final String description;
}
