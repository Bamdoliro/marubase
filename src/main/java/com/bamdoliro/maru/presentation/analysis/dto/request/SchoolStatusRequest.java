package com.bamdoliro.maru.presentation.analysis.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStatusRequest {

    @NotNull(message = "필수값입니다.")
    private List<FormStatus> statusList;

    @NotNull(message = "필수값입니다.")
    private Boolean isBusan;

    @Nullable
    private String gu;
}