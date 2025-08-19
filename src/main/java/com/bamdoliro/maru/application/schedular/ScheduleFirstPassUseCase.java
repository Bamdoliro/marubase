package com.bamdoliro.maru.application.schedular;

import com.bamdoliro.maru.infrastructure.scheduler.FormScheduler;
import com.bamdoliro.maru.presentation.scheduler.dto.request.ScheduleRequest;
import com.bamdoliro.maru.presentation.scheduler.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class ScheduleFirstPassUseCase {

    private final FormScheduler formScheduler;

    public ScheduleResponse execute(ScheduleRequest request) {
        formScheduler.selectFirstPass(request.getScheduleSelectFirstPass());
        return new ScheduleResponse(request.getScheduleSelectFirstPass());
    }
}
