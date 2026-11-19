package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.exception.NoSelectionCandidatesException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.domain.FirstPassSelectionExecution;
import com.bamdoliro.maru.domain.schedule.exception.FirstPassAlreadyExecutedException;
import com.bamdoliro.maru.infrastructure.persistence.schedule.FirstPassSelectionExecutionRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class ExecuteFirstPassSelectionUseCase {

    private final AdmissionScheduleFacade admissionScheduleFacade;
    private final FirstPassSelectionExecutionRepository executionRepository;
    private final SelectFirstPassUseCase selectFirstPassUseCase;
    private final FormRepository formRepository;

    @Transactional
    public void execute() {
        AdmissionSchedule schedule = admissionScheduleFacade.getCurrentScheduleForUpdate();
        if (executionRepository.existsBySchedule(schedule)) {
            throw new FirstPassAlreadyExecutedException();
        }

        if (formRepository.findByStatus(FormStatus.APPROVED).isEmpty()) {
            throw new NoSelectionCandidatesException();
        }

        executionRepository.save(new FirstPassSelectionExecution(schedule));
        selectFirstPassUseCase.execute();
    }
}
