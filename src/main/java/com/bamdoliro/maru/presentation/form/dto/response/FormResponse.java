package com.bamdoliro.maru.presentation.form.dto.response;


import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import lombok.Getter;

@Getter
public class FormResponse {

    private Long id;
    private String name;
    private FormStatus status;

    public FormResponse(Form form) {
        this.id = form.getId();
        this.name = form.getApplicant().getName();
        this.status = form.getStatus();
    }
}
