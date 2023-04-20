package com.bamdoliro.maru.presentation.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserRequest {

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    // 최소 8 자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$",
            message = "올바른 형식의 비밀번호여야 합니다.")
    private String password;
}
