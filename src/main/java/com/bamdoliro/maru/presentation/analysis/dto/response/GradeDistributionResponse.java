package com.bamdoliro.maru.presentation.analysis.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.GradeVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GradeDistributionResponse {
    private FormType type;
    private Double firstRoundMax;
    private Double firstRoundMin;
    private Double firstRoundAvg;
    private Double totalMax;
    private Double totalMin;
    private Double totalAvg;

    public GradeDistributionResponse(GradeVo vo) {
        this.type = vo.getType();
        this.firstRoundMax = vo.getFirstRoundMax();
        this.firstRoundMin = vo.getFirstRoundMin();
        this.firstRoundAvg = vo.getFirstRoundAvg();
        this.totalMax = vo.getTotalMax();
        this.totalMin = vo.getTotalMin();
        this.totalAvg = vo.getTotalAvg();
    }
}