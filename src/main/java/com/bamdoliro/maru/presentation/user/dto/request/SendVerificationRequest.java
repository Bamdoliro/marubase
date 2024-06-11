package com.bamdoliro.maru.presentation.user.dto.request;

import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(min = 11, max = 11, message = "11글자여야 합니다.")
    private String phoneNumber;

    @NotNull(message = "필수값입니다.")
    private VerificationType type;
}
