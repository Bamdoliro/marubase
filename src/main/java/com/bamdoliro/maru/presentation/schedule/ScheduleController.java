package com.bamdoliro.maru.presentation.schedule;

import com.bamdoliro.maru.application.schedule.QueryCurrentScheduleUseCase;
import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/schedules")
@RestController
public class ScheduleController {

    private final QueryCurrentScheduleUseCase queryCurrentScheduleUseCase;

    @GetMapping("/current")
    public SingleCommonResponse<ScheduleResponse> getCurrentSchedule() {
        return CommonResponse.ok(queryCurrentScheduleUseCase.execute());
    }
}
