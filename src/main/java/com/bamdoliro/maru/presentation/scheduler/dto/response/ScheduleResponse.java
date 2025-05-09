package com.bamdoliro.maru.presentation.scheduler.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class ScheduleResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduleSelectFirstPass;

    public ScheduleResponse(LocalDateTime scheduleSelectFirstPass) {
        this.scheduleSelectFirstPass = scheduleSelectFirstPass;
    }
}
