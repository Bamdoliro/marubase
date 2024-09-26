package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.Form;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormResultResponse {

    private Long id;
    private String name;
    private String type;
    private boolean isPassed;
    private boolean isChangedToRegular;

    public FormResultResponse(Form form, boolean isPassed) {
        this.id = form.getId();
        this.name = form.getApplicant().getName();
        this.type = form.getType().getDescription();
        this.isPassed = isPassed;
        this.isChangedToRegular= form.getChangedToRegular();
    }
}
