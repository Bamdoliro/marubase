package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FormRequest {

    @Valid
    private ApplicantRequest applicant;

    @Valid
    private ParentRequest parent;

    @Valid
    private EducationRequest education;

    @Valid
    private GradeRequest grade;

    @Valid
    private DocumentRequest document;

    @NotNull(message = "필수값입니다.")
    private FormType type;
}
