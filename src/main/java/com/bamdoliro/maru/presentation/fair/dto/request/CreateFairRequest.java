package com.bamdoliro.maru.presentation.fair.dto.request;

import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFairRequest {

    @NotNull(message = "필수값입니다.")
    private LocalDateTime start;

    @NotNull(message = "필수값입니다.")
    @Min(value = 1, message = "1 이상이어야 합니다.")
    private Integer capacity;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 30, message = "30자 이하여야 합니다.")
    private String place;

    @NotBlank(message = "필수값입니다.")
    private String applicationUrl;

    @NotNull(message = "필수값입니다.")
    private FairType type;

    @Nullable
    private LocalDate applicationStartDate;

    @Nullable
    private LocalDate applicationEndDate;
}
