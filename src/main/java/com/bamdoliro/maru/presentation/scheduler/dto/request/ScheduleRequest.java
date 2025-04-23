package com.bamdoliro.maru.presentation.scheduler.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleRequest {

    @NotBlank(message = "필수값입니다.")
    private LocalDateTime schedule_select_first_pass;
}
