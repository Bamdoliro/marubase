package com.bamdoliro.maru.presentation.policy.dto;

import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import com.bamdoliro.maru.domain.policy.TicketNotice;

public record PolicyResponse(
        Long scheduleId, Integer admissionYear, Long version,
        boolean selectionLocked, SelectionPolicy selection, TicketNotice ticket
) {
}
