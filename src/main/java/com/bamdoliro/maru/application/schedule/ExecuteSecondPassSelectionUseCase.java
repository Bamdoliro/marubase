package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.form.SelectSecondPassUseCase;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.exception.NoSelectionCandidatesException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class ExecuteSecondPassSelectionUseCase {

    private final AdmissionScheduleFacade admissionScheduleFacade;
    private final FormRepository formRepository;
    private final SelectSecondPassUseCase selectSecondPassUseCase;

    @Transactional
    public void execute() {
        admissionScheduleFacade.getCurrentScheduleForUpdate();
        // 선행 요청이 완료되면 FIRST_PASSED 대상이 남지 않으므로 재선발하지 않는다.
        if (formRepository.findByStatus(FormStatus.FIRST_PASSED).isEmpty()) {
            throw new NoSelectionCandidatesException();
        }
        selectSecondPassUseCase.execute();
    }
}
