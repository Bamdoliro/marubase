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
        this.firstRoundMax = MathUtil.roundTo(vo.getFirstRoundMax(), 3);
        this.firstRoundMin = MathUtil.roundTo(vo.getFirstRoundMin(), 3);
        this.firstRoundAvg = MathUtil.roundTo(vo.getFirstRoundAvg(), 3);
        this.totalMax = MathUtil.roundTo(vo.getTotalMax(), 3);
        this.totalMin = MathUtil.roundTo(vo.getTotalMin(), 3);
        this.totalAvg = MathUtil.roundTo(vo.getTotalAvg(), 3);
    }
}