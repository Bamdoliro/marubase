package com.bamdoliro.maru.presentation.analysis.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeDistributionRequest {

    @NotNull(message = "필수값입니다.")
    List<FormStatus> statusList;
}