package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.schedule.event.AdmissionScheduleChangedEvent;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.domain.type.ScheduleChangeType;
import com.bamdoliro.maru.domain.schedule.exception.ScheduleAlreadyExistsException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.aop.log.AdmissionScheduleAuditService;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.presentation.schedule.dto.request.ScheduleRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.response.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreateScheduleUseCase {

    private final AdmissionScheduleRepository admissionScheduleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AdmissionScheduleAuditService auditService;

    @Transactional
    public IdResponse execute(User user, ScheduleRequest request) {
        if (admissionScheduleRepository.existsByAdmissionYear(request.getAdmissionYear())) {
            throw new ScheduleAlreadyExistsException();
        }

        admissionScheduleRepository.findByActiveTrue().ifPresent(schedule -> {
            schedule.deactivate();
            admissionScheduleRepository.saveAndFlush(schedule);
        });
        AdmissionSchedule schedule = admissionScheduleRepository.save(createSchedule(request));
        auditService.log(
                schedule,
                user,
                ScheduleChangeType.CREATED,
                null,
                auditService.snapshot(schedule)
        );
        eventPublisher.publishEvent(new AdmissionScheduleChangedEvent(schedule.getId()));
        return new IdResponse(schedule.getId());
    }

    private AdmissionSchedule createSchedule(ScheduleRequest request) {
        return AdmissionSchedule.builder()
                .admissionYear(request.getAdmissionYear())
                .active(true)
                .applicationFormStart(request.getApplicationFormStart())
                .applicationFormEnd(request.getApplicationFormEnd())
                .selectFirstPassAt(request.getSelectFirstPassAt())
                .announcementOfFirstPass(request.getAnnouncementOfFirstPass())
                .announcementOfSecondPass(request.getAnnouncementOfSecondPass())
                .codingTest(request.getCodingTest())
                .ncs(request.getNcs())
                .depthInterview(request.getDepthInterview())
                .physicalExamination(request.getPhysicalExamination())
                .entranceRegistrationPeriodStart(request.getEntranceRegistrationPeriodStart())
                .entranceRegistrationPeriodEnd(request.getEntranceRegistrationPeriodEnd())
                .meisterTalentEntranceTime(request.getMeisterTalentEntranceTime())
                .meisterTalentExclusionEntranceTime(request.getMeisterTalentExclusionEntranceTime())
                .admissionAndPledgeStart(request.getAdmissionAndPledgeStart())
                .admissionAndPledgeEnd(request.getAdmissionAndPledgeEnd())
                .build();
    }
}
