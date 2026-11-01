package com.bamdoliro.maru.presentation.policy.dto;

import com.bamdoliro.maru.domain.policy.AdmissionPolicyChangeLog;
import java.time.LocalDateTime;

public record PolicyHistoryResponse(
        Long id, Long userId, LocalDateTime createdAt, String beforePolicy, String afterPolicy
) {
    public static PolicyHistoryResponse from(AdmissionPolicyChangeLog log) {
        return new PolicyHistoryResponse(log.getId(), log.getUserId(), log.getCreatedAt(),
                log.getBeforePolicy(), log.getAfterPolicy());
    }
}
