package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.schedule.event.AdmissionScheduleChangedEvent;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.exception.ScheduleVersionConflictException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.aop.log.AdmissionScheduleAuditService;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.FirstPassSelectionExecutionRepository;
import com.bamdoliro.maru.presentation.schedule.dto.request.ScheduleRequest;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateScheduleUseCaseTest {

    @InjectMocks
    private UpdateScheduleUseCase updateScheduleUseCase;

    @Mock
    private AdmissionScheduleFacade admissionScheduleFacade;

    @Mock
    private AdmissionScheduleRepository admissionScheduleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private FirstPassSelectionExecutionRepository executionRepository;

    @Mock
    private AdmissionScheduleAuditService auditService;

    @Test
    void 현재_입학_일정을_수정한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        ScheduleRequest request = ScheduleFixture.createScheduleRequest(null);
        User user = UserFixture.createAdminUser();
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        given(auditService.snapshot(schedule)).willReturn("schedule");

        updateScheduleUseCase.execute(user, request);

        verify(auditService).log(any(), any(), any(), any(), any());
        verify(eventPublisher).publishEvent(any(AdmissionScheduleChangedEvent.class));
    }

    @Test
    void 수정_버전이_다르면_에러가_발생한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        ScheduleRequest request = ScheduleFixture.createScheduleRequest(1L);
        User user = UserFixture.createAdminUser();
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        given(auditService.snapshot(schedule)).willReturn("schedule");

        assertThrows(
                ScheduleVersionConflictException.class,
                () -> updateScheduleUseCase.execute(user, request)
        );

        verify(eventPublisher, never()).publishEvent(any());
    }
}
