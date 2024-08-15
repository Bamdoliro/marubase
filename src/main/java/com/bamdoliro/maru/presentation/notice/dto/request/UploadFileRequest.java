package com.bamdoliro.maru.presentation.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileRequest {

    @NotBlank(message = "필수값입니다.")
    private String fileName;
}
