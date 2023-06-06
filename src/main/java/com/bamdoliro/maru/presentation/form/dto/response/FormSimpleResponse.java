package com.bamdoliro.maru.presentation.form.dto.response;


import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormSimpleResponse {

    private Long id;
    private String name;
    private FormStatus status;

    public FormSimpleResponse(Form form) {
        this.id = form.getId();
        this.name = form.getApplicant().getName();
        this.status = form.getStatus();
    }
}
