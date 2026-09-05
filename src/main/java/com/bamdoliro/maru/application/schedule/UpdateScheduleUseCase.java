package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.schedule.event.AdmissionScheduleChangedEvent;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.domain.type.ScheduleChangeType;
import com.bamdoliro.maru.domain.schedule.exception.FirstPassAlreadyExecutedException;
import com.bamdoliro.maru.domain.schedule.exception.ScheduleAlreadyExistsException;
import com.bamdoliro.maru.domain.schedule.exception.ScheduleVersionConflictException;
import com.bamdoliro.maru.infrastructure.aop.log.AdmissionScheduleAuditService;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.FirstPassSelectionExecutionRepository;
import com.bamdoliro.maru.presentation.schedule.dto.request.ScheduleRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.auth.AuthenticatedUser;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdateScheduleUseCase {

    private final AdmissionScheduleFacade admissionScheduleFacade;
    private final AdmissionScheduleRepository admissionScheduleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FirstPassSelectionExecutionRepository executionRepository;
    private final AdmissionScheduleAuditService auditService;

    @Transactional
    public void execute(AuthenticatedUser user, ScheduleRequest request) {
        AdmissionSchedule schedule = admissionScheduleFacade.getCurrentSchedule();
        String beforeSchedule = auditService.snapshot(schedule);
        validateVersion(schedule, request.getVersion());
        validateAdmissionYear(schedule, request.getAdmissionYear());
        validateFirstPassSelection(schedule, request.getSelectFirstPassAt());

        schedule.update(
                request.getAdmissionYear(),
                request.getApplicationFormStart(),
                request.getApplicationFormEnd(),
                request.getSelectFirstPassAt(),
                request.getAnnouncementOfFirstPass(),
                request.getAnnouncementOfSecondPass(),
                request.getCodingTest(),
                request.getNcs(),
                request.getDepthInterview(),
                request.getPhysicalExamination(),
                request.getEntranceRegistrationPeriodStart(),
                request.getEntranceRegistrationPeriodEnd(),
                request.getMeisterTalentEntranceTime(),
                request.getMeisterTalentExclusionEntranceTime(),
                request.getAdmissionAndPledgeStart(),
                request.getAdmissionAndPledgeEnd()
        );
        auditService.log(
                schedule,
                user.id(),
                ScheduleChangeType.UPDATED,
                beforeSchedule,
                auditService.snapshot(schedule)
        );
        eventPublisher.publishEvent(new AdmissionScheduleChangedEvent(schedule.getId()));
    }

    private void validateFirstPassSelection(
            AdmissionSchedule schedule,
            LocalDateTime executionTime
    ) {
        if (!Objects.equals(schedule.getSelectFirstPassAt(), executionTime)
                && executionRepository.existsBySchedule(schedule)) {
            throw new FirstPassAlreadyExecutedException();
        }
    }

    private void validateVersion(AdmissionSchedule schedule, Long version) {
        if (!Objects.equals(schedule.getVersion(), version)) {
            throw new ScheduleVersionConflictException();
        }
    }

    private void validateAdmissionYear(AdmissionSchedule schedule, Integer admissionYear) {
        if (!Objects.equals(schedule.getAdmissionYear(), admissionYear)
                && admissionScheduleRepository.existsByAdmissionYear(admissionYear)) {
            throw new ScheduleAlreadyExistsException();
        }
    }
}
