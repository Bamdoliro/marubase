package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormUrlResponse {

    private Long examinationNumber;
    private String name;
    private String formUrl;

    public FormUrlResponse(FormUrlVo vo) {
        this.examinationNumber = vo.getExaminationNumber();
        this.name = vo.getName();
        this.formUrl = vo.getUuid();
    }
}
