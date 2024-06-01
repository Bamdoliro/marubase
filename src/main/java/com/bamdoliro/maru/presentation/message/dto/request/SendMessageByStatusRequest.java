package com.bamdoliro.maru.presentation.message.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageByStatusRequest {

    @NotBlank(message = "필수값입니다.")
    private String title;

    @NotBlank(message = "필수값입니다.")
    private String text;

    @NotNull(message = "필수값입니다.")
    private FormStatus status;
}
