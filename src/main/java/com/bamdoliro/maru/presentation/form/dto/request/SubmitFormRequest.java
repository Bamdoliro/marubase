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
public class SubmitFormRequest {

    @Valid
    @NotNull(message = "필수값입니다.")
    private ApplicantRequest applicant;

    @Valid
    @NotNull(message = "필수값입니다.")
    private ParentRequest parent;

    @Valid
    @NotNull(message = "필수값입니다.")
    private EducationRequest education;

    @Valid
    @NotNull(message = "필수값입니다.")
    private GradeRequest grade;

    @Valid
    @NotNull(message = "필수값입니다.")
    private DocumentRequest document;

    @NotNull(message = "필수값입니다.")
    private FormType type;
}
