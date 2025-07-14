package com.bamdoliro.maru.presentation.scheduler;

import com.bamdoliro.maru.application.schedular.ScheduleFirstPassUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.scheduler.dto.request.ScheduleRequest;
import com.bamdoliro.maru.presentation.scheduler.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class SchedulerController {

    private final ScheduleFirstPassUseCase scheduleFirstPassUseCase;


    @PostMapping("/first-pass")
    public SingleCommonResponse<ScheduleResponse> updateSchedule(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid ScheduleRequest scheduleRequest) {

        return CommonResponse.ok(
                new ScheduleResponse(scheduleFirstPassUseCase.execute(scheduleRequest.getScheduleSelectFirstPass())));
    }
}
