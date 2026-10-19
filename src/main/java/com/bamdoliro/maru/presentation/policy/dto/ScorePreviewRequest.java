package com.bamdoliro.maru.presentation.policy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;

public record ScorePreviewRequest(
        @NotBlank @Size(max = 8192) String source,
        @NotNull @Size(max = 40) Map<String, BigDecimal> inputs
) {
}
