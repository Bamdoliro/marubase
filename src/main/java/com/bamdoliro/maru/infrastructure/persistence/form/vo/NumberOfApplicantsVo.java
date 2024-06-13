package com.bamdoliro.maru.infrastructure.persistence.form.vo;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class NumberOfApplicantsVo {
    private FormType type;
    private Long count;

    @QueryProjection
    public NumberOfApplicantsVo(FormType type, Long count) {
        this.type = type;
        this.count = count;
    }
}