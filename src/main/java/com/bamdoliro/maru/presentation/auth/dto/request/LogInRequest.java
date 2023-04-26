package com.bamdoliro.maru.presentation.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogInRequest {

    @NotBlank(message = "필수값입니다.")
    @Email(message = "올바른 형식의 이메일이어야 합니다.")
    private String email;

    @NotBlank(message = "필수값입니다.")
    private String password;
}
