package com.bamdoliro.maru.presentation.scheduler.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ScheduleResponse {

    private LocalDateTime schedule_select_first_pass;

    public ScheduleResponse(LocalDateTime schedule_select_first_pass) {
        this.schedule_select_first_pass = schedule_select_first_pass;
    }
}
