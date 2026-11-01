package com.bamdoliro.maru.presentation.policy.dto;

import com.bamdoliro.maru.domain.policy.formula.ScoreSubject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public record ScorePreviewRequest(
        @NotBlank @Size(max = 8192) String source,
        @NotNull @Size(max = 40) Map<String, BigDecimal> inputs,
        @Size(max = 240) List<@NotNull @Valid ScoreSubject> subjects
) {
    public ScorePreviewRequest {
        subjects = subjects == null ? List.of() : subjects;
    }

    public ScorePreviewRequest(String source, Map<String, BigDecimal> inputs) {
        this(source, inputs, List.of());
    }
}
