package com.bamdoliro.maru.infrastructure.persistence.form.vo;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FormUrlVo {

    private Long examinationNumber;
    private String name;
    private String uuid;

    @QueryProjection
    public FormUrlVo(Long examinationNumber, String name, UUID uuid) {
        this.examinationNumber = examinationNumber;
        this.name = name;
        this.uuid = uuid.toString();
    }
}
