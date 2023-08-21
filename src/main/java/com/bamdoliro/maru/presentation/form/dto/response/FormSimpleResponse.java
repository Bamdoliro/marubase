package com.bamdoliro.maru.presentation.form.dto.response;


import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FormSimpleResponse {

    private Long id;
    private String name;
    private LocalDate birthday;
    private GraduationType graduationType;
    private String school;
    private String status;
    private FormType type;

    public FormSimpleResponse(Form form) {
        this.id = form.getId();
        this.name = form.getApplicant().getName();
        this.birthday = form.getApplicant().getBirthday();
        this.graduationType = form.getEducation().getGraduationType();
        this.school = form.getEducation().getSchool().getName();
        this.status = form.getStatus().getDescription();
        this.type = form.getType();
    }
}
