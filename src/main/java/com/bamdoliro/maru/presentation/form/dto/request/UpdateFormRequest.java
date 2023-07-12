package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFormRequest {

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

    @NotBlank(message = "필수값입니다.")
    @Size(max = 150, message = "150자 이하여야 합니다.")
    @URL
    private String formUrl;

    @NotNull(message = "필수값입니다.")
    private FormType type;
}
