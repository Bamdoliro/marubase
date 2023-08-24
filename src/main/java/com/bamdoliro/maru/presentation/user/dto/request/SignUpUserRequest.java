package com.bamdoliro.maru.presentation.user.dto.request;

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

    @NotBlank(message = "필수값입니다.")
    @Size(min = 11, max = 11, message = "11자여야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 50, message = "50자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 6, max = 6, message = "6자여야 합니다.")
    private String code;

    @NotBlank(message = "필수값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 하나의 문자, 숫자, 특수문자를 포함하며 8 글자 이상이어야 합니다.")
    private String password;
}
