package com.bamdoliro.maru.presentation.form.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParentRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 11, max = 11, message = "11자여야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 5, max = 5, message = "5자여야 합니다.")
    private String zoneCode;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 100, message = "100자 이하여야 합니다.")
    private String address;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 100, message = "100자 이하여야 합니다.")
    private String detailAddress;
}
