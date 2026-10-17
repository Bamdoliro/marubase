package com.bamdoliro.maru.presentation.fair.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendAdmissionFairRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 30, message = "30자 이하여야 합니다.")
    private String schoolName;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 20, message = "20자 이하여야 합니다.")
    @Pattern(
            regexp = "^[\\p{L}\\p{M}]+(?:['-][\\p{L}\\p{M}]+)*(?:\\.)?"
                    + "(?: [\\p{L}\\p{M}]+(?:['-][\\p{L}\\p{M}]+)*(?:\\.)?)*$",
            message = "이름에는 문자, 공백, 하이픈, 아포스트로피와 단어 끝의 마침표만 사용할 수 있습니다."
    )
    private String name;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String type;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 11, max = 11, message = "11자여야 합니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "010으로 시작하는 숫자 11자리여야 합니다.")
    private String phoneNumber;

    @NotNull(message = "필수값입니다.")
    @Min(value = 1, message = "1명 이상 신청해야 합니다.")
    @Max(value = 10, message = "10명 이하로 신청해야 합니다.")
    private Integer headcount;

    @Nullable
    @Size(max = 255, message = "255자 이하여야 합니다.")
    private String question;
}
