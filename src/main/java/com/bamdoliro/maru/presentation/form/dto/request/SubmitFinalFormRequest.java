package com.bamdoliro.maru.presentation.form.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitFinalFormRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 150, message = "150자 이하여야 합니다.")
    @URL(message = "올바른 URL 형식이어야 합니다.")
    private String formUrl;
}
