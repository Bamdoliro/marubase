package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class QueryScheduleUseCase {

    private final AdmissionScheduleFacade admissionScheduleFacade;

    @Transactional(readOnly = true)
    public ScheduleResponse execute(Integer admissionYear) {
        return new ScheduleResponse(admissionScheduleFacade.getSchedule(admissionYear));
    }
}
