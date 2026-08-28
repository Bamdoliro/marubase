package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
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

    @Transactional
    public void execute(Long scheduleId) {
        AdmissionSchedule schedule = admissionScheduleFacade.getSchedule(scheduleId);
        if (executionRepository.existsBySchedule(schedule)) {
            throw new FirstPassAlreadyExecutedException();
        }

        executionRepository.save(new FirstPassSelectionExecution(schedule));
        selectFirstPassUseCase.execute();
    }
}
