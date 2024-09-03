package com.bamdoliro.maru.presentation.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageToAllUserRequest {

    @NotBlank(message = "필수값입니다.")
    private String title;

    @NotBlank(message = "필수값입니다.")
    private String text;
}
