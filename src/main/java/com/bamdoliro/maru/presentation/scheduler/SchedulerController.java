package com.bamdoliro.maru.presentation.scheduler;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.scheduler.FormScheduler;
import com.bamdoliro.maru.presentation.scheduler.dto.request.ScheduleRequest;
import com.bamdoliro.maru.presentation.scheduler.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SchedulerController {

    private final FormScheduler formScheduler;


    @PostMapping("/update-schedule")
    public ScheduleResponse updateSchedule(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid ScheduleRequest scheduleRequest) {

        formScheduler.selectFirstPass(scheduleRequest.getSchedule_select_first_pass());

        return new ScheduleResponse(scheduleRequest.getSchedule_select_first_pass());
    }
}
