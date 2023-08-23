package com.bamdoliro.maru.presentation.fair.dto.request;

import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFairRequest {

    @NotNull(message = "필수값입니다.")
    private LocalDateTime start;

    @NotNull(message = "필수값입니다.")
    private Integer capacity;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 30, message = "최대 30자까지 입력 가능합니다.")
    private String place;

    @NotNull(message = "필수값입니다.")
    private FairType type;
}
