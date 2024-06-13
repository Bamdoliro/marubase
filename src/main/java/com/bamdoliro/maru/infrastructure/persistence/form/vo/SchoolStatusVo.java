package com.bamdoliro.maru.infrastructure.persistence.form.vo;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class SchoolStatusVo {
    private String applicantName;
    private String schoolName;
    private String schoolAddress;

    @QueryProjection
    public SchoolStatusVo(String applicantName, String schoolName, String schoolAddress) {
        this.applicantName = applicantName;
        this.schoolName = schoolName;
        this.schoolAddress = schoolAddress;
    }
}