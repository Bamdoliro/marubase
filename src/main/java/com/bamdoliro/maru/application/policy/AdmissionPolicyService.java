package com.bamdoliro.maru.application.policy;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.policy.AdmissionPolicy;
import com.bamdoliro.maru.domain.policy.AdmissionPolicyChangeLog;
import com.bamdoliro.maru.domain.policy.PolicyException;
import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import com.bamdoliro.maru.domain.policy.TicketNotice;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.policy.AdmissionPolicyChangeLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.policy.AdmissionPolicyRepository;
import com.bamdoliro.maru.presentation.policy.dto.PolicyHistoryResponse;
import com.bamdoliro.maru.presentation.policy.dto.PolicyRequest;
import com.bamdoliro.maru.presentation.policy.dto.PolicyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdmissionPolicyService {
    private final AdmissionScheduleFacade scheduleFacade;
    private final AdmissionPolicyRepository policyRepository;
    private final AdmissionPolicyChangeLogRepository historyRepository;
    private final FormRepository formRepository;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public PolicyResponse current() {
        return read(scheduleFacade.getCurrentSchedule());
    }

    @Transactional(readOnly = true)
    public PolicyResponse forYear(Integer year) {
        return read(scheduleFacade.getSchedule(year));
    }

    @Transactional(readOnly = true)
    public TicketNotice ticketFor(Long scheduleId) {
        return policyRepository.findById(scheduleId)
                .map(policy -> decode(policy.getTicketJson(), TicketNotice.class))
                .orElseGet(TicketNotice::defaults);
    }

    @Transactional(readOnly = true)
    public List<PolicyHistoryResponse> history(Integer year) {
        Long scheduleId = scheduleFacade.getSchedule(year).getId();
        return historyRepository.findTop50ByScheduleIdOrderByIdDesc(scheduleId).stream()
                .map(PolicyHistoryResponse::from).toList();
    }

    @Transactional
    public PolicyResponse update(Long userId, PolicyRequest request) {
        AdmissionSchedule schedule = lockCurrentSchedule();
        PolicyResponse before = read(schedule);
        if (!Objects.equals(before.scheduleId(), request.scheduleId())
                || !Objects.equals(before.version(), request.version())) {
            throw new PolicyException(PolicyException.Reason.POLICY_VERSION_CONFLICT);
        }
        if (request.selection().total() < 1 || request.selection().total() > 1000) {
            throw new PolicyException(PolicyException.Reason.INVALID_POLICY);
        }
        // JSON의 소수점 표기 차이(1.3/1.30)는 같은 정책으로 취급한다.
        if (before.selectionLocked() && !sameSelection(before.selection(), request.selection())) {
            throw new PolicyException(PolicyException.Reason.SELECTION_POLICY_LOCKED);
        }
        AdmissionPolicy policy = policyRepository.findById(schedule.getId())
                .orElseGet(() -> defaults(schedule.getId()));
        policy.update(encode(request.selection()), encode(request.ticket()));
        if (before.selectionLocked()) {
            policy.lockSelection();
        }
        policyRepository.saveAndFlush(policy);
        PolicyResponse after = read(schedule);
        historyRepository.save(new AdmissionPolicyChangeLog(
                schedule.getId(), userId, encode(before), encode(after)));
        return after;
    }

    /** 선발 실행과 관리자 수정은 같은 일정 행을 잠가 직렬화한다. */
    @Transactional
    public SelectionPolicy selectionForExecution() {
        AdmissionSchedule schedule = lockCurrentSchedule();
        AdmissionPolicy policy = policyRepository.findById(schedule.getId())
                .orElseGet(() -> defaults(schedule.getId()));
        policy.lockSelection();
        policyRepository.saveAndFlush(policy);
        return decode(policy.getSelectionJson(), SelectionPolicy.class);
    }

    private AdmissionSchedule lockCurrentSchedule() {
        AdmissionSchedule schedule = scheduleFacade.getCurrentSchedule();
        entityManager.refresh(schedule, LockModeType.PESSIMISTIC_WRITE);
        if (!schedule.isActive()) {
            throw new PolicyException(PolicyException.Reason.POLICY_VERSION_CONFLICT);
        }
        return schedule;
    }

    private PolicyResponse read(AdmissionSchedule schedule) {
        AdmissionPolicy policy = policyRepository.findById(schedule.getId()).orElse(null);
        boolean locked = (policy != null && policy.isSelectionLocked())
                || !LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(schedule.getApplicationFormStart())
                || formRepository.existsByIdNotNull();
        return new PolicyResponse(schedule.getId(), schedule.getAdmissionYear(),
                policy == null ? -1L : policy.getVersion(), locked,
                policy == null ? SelectionPolicy.defaults() : decode(policy.getSelectionJson(), SelectionPolicy.class),
                policy == null ? TicketNotice.defaults() : decode(policy.getTicketJson(), TicketNotice.class));
    }

    private AdmissionPolicy defaults(Long scheduleId) {
        return new AdmissionPolicy(scheduleId, encode(SelectionPolicy.defaults()), encode(TicketNotice.defaults()));
    }

    private boolean sameSelection(SelectionPolicy left, SelectionPolicy right) {
        return Objects.equals(left.regular(), right.regular()) && Objects.equals(left.meisterTalent(), right.meisterTalent())
                && Objects.equals(left.socialIntegration(), right.socialIntegration())
                && Objects.equals(left.nationalVeteransEducation(), right.nationalVeteransEducation())
                && Objects.equals(left.specialAdmission(), right.specialAdmission())
                && left.firstRoundMultiplier().compareTo(right.firstRoundMultiplier()) == 0
                && left.otherRegionRate().compareTo(right.otherRegionRate()) == 0;
    }

    private String encode(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("입학 정책 직렬화 실패", exception);
        }
    }

    private <T> T decode(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException exception) {
            // 손상된 정책을 기본값으로 숨기면 선발 결과가 달라질 수 있다.
            throw new IllegalStateException("입학 정책 역직렬화 실패", exception);
        }
    }
}
