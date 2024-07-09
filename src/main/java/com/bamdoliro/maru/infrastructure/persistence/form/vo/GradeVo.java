package com.bamdoliro.maru.infrastructure.persistence.form.vo;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class GradeVo {
    private FormType type;
    private Double firstRoundMax;
    private Double firstRoundMin;
    private Double firstRoundAvg;
    private Double totalMax;
    private Double totalMin;
    private Double totalAvg;

    @QueryProjection
    public GradeVo(FormType type, Double firstRoundMax, Double firstRoundMin, Double firstRoundAvg, Double totalMax, Double totalMin, Double totalAvg) {
        this.type = type;
        this.firstRoundMax = firstRoundMax;
        this.firstRoundMin = firstRoundMin;
        this.firstRoundAvg = firstRoundAvg;
        this.totalMax = totalMax;
        this.totalMin = totalMin;
        this.totalAvg = totalAvg;
    }
}