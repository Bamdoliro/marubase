package com.bamdoliro.maru.presentation.analysis.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.GradeVo;
import com.bamdoliro.maru.shared.util.MathUtil;
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
        this.firstRoundMax = MathUtil.round(vo.getFirstRoundMax());
        this.firstRoundMin = MathUtil.round(vo.getFirstRoundMin());
        this.firstRoundAvg = MathUtil.round(vo.getFirstRoundAvg());
        this.totalMax = MathUtil.round(vo.getTotalMax());
        this.totalMin = MathUtil.round(vo.getTotalMin());
        this.totalAvg = MathUtil.round(vo.getTotalAvg());
    }
}