package com.bamdoliro.maru.presentation.form.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 1600, message = "1600자 이하여야 합니다.")
    private String coverLetter;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 1600, message = "1600자 이하여야 합니다.")
    private String statementOfPurpose;
}
