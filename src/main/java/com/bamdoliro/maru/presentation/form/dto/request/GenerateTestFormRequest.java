package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTestFormRequest {

    @NotNull(message = "필수값입니다.")
    private FormType type;

    @NotNull(message = "필수값입니다.")
    @Min(value = 1, message = "1 이상이어야 합니다.")
    @Max(value = 500, message = "500 이하여야 합니다.")
    private Integer count;
}
