package com.bamdoliro.maru.infrastructure.persistence.form.vo;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FormUrlVo {

    private Long examinationNumber;
    private String name;
    private String formUrl;

    @QueryProjection
    public FormUrlVo(Long examinationNumber, String name, String formUrl) {
        this.examinationNumber = examinationNumber;
        this.name = name;
        this.formUrl = formUrl;
    }
}
