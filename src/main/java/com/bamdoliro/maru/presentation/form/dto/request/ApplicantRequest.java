package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantRequest {

    // TODO :: 증명사진 추가

    @NotBlank(message = "필수값입니다.")
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 11, max = 11, message = "11자여야 합니다.")
    private String phoneNumber;

    @NotNull(message = "필수값입니다.")
    @Past(message = "과거여야 합니다.")
    private LocalDate birthDay;

    @NotNull(message = "필수값입니다.")
    private Gender gender;
}
