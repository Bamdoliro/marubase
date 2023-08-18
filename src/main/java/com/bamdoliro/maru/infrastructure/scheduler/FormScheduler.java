package com.bamdoliro.maru.infrastructure.scheduler;

import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
import com.bamdoliro.maru.shared.constants.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FormScheduler {

    private final SelectFirstPassUseCase selectFirstPassUseCase;

    @Scheduled(cron = Schedule.SELECT_FIRST_PASS_CRON)
    public void selectFirstPass() {
        selectFirstPassUseCase.execute();
    }
}
