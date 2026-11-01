package com.bamdoliro.maru.presentation.policy.dto;

import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import com.bamdoliro.maru.domain.policy.TicketNotice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PolicyRequest(
        @NotNull Long scheduleId,
        @NotNull @Min(-1) Long version,
        @NotNull @Valid SelectionPolicy selection,
        @NotNull @Valid TicketNotice ticket
) {
}
