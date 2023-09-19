package com.bamdoliro.maru.presentation.form.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PassOrFailFormRequest {

    @Min(value = 1, message = "1 이상이어야 합니다.")
    @Max(value = 10000, message = "10000 이하여야 합니다.")
    @NotNull(message = "필수값입니다.")
    private Long formId;

    @NotNull(message = "필수값입니다.")
    private boolean pass;
}
