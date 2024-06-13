package com.bamdoliro.maru.presentation.analysis.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.NumberOfApplicantsVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NumberOfApplicantsResponse {
    private FormType type;
    private Long count;

    public NumberOfApplicantsResponse(NumberOfApplicantsVo vo){
        this.type = vo.getType();
        this.count = vo.getCount();
    }
}