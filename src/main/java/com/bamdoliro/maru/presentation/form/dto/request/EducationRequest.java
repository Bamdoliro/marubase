package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EducationRequest {

    @NotNull(message = "필수값입니다.")
    private GraduationType graduationType;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 4, max = 4, message = "4자여야 합니다.")
    private String graduationYear;
}
