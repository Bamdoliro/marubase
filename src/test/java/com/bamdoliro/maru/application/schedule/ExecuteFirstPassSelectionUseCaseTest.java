package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.NoSelectionCandidatesException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import java.util.List;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.domain.FirstPassSelectionExecution;
import com.bamdoliro.maru.domain.schedule.exception.FirstPassAlreadyExecutedException;
import com.bamdoliro.maru.infrastructure.persistence.schedule.FirstPassSelectionExecutionRepository;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExecuteFirstPassSelectionUseCaseTest {

    @InjectMocks
    private ExecuteFirstPassSelectionUseCase executeFirstPassSelectionUseCase;

    @Mock
    private AdmissionScheduleFacade admissionScheduleFacade;

    @Mock
    private FirstPassSelectionExecutionRepository executionRepository;

    @Mock
    private SelectFirstPassUseCase selectFirstPassUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 일차_합격자를_한_번_선발한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(admissionScheduleFacade.getCurrentScheduleForUpdate()).willReturn(schedule);
        given(formRepository.findByStatus(FormStatus.APPROVED))
                .willReturn(List.of(FormFixture.createForm(FormType.REGULAR)));

        executeFirstPassSelectionUseCase.execute();

        verify(executionRepository).save(any(FirstPassSelectionExecution.class));
        verify(selectFirstPassUseCase).execute();
    }

    @Test
    void 이미_선발한_일정은_다시_실행하지_않는다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        given(admissionScheduleFacade.getCurrentScheduleForUpdate()).willReturn(schedule);
        given(executionRepository.existsBySchedule(schedule)).willReturn(true);

        assertThrows(
                FirstPassAlreadyExecutedException.class,
                () -> executeFirstPassSelectionUseCase.execute()
        );

        verify(selectFirstPassUseCase, never()).execute();
    }

    @Test
    void 승인_원서가_없으면_실행기록을_남기지_않는다() {
        given(admissionScheduleFacade.getCurrentScheduleForUpdate()).willReturn(ScheduleFixture.createSchedule());
        given(formRepository.findByStatus(FormStatus.APPROVED)).willReturn(List.of());
        assertThrows(NoSelectionCandidatesException.class, executeFirstPassSelectionUseCase::execute);
        verify(executionRepository, never()).save(any());
        verify(selectFirstPassUseCase, never()).execute();
    }
}
