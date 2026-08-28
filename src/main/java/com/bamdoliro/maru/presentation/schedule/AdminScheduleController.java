package com.bamdoliro.maru.presentation.schedule;

import com.bamdoliro.maru.application.schedule.CreateScheduleUseCase;
import com.bamdoliro.maru.application.schedule.QueryCurrentScheduleUseCase;
import com.bamdoliro.maru.application.schedule.QueryScheduleUseCase;
import com.bamdoliro.maru.application.schedule.UpdateScheduleUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.schedule.dto.request.ScheduleRequest;
import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.IdResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/schedules")
@RestController
public class AdminScheduleController {

    private final QueryCurrentScheduleUseCase queryCurrentScheduleUseCase;
    private final QueryScheduleUseCase queryScheduleUseCase;
    private final CreateScheduleUseCase createScheduleUseCase;
    private final UpdateScheduleUseCase updateScheduleUseCase;

    @GetMapping("/current")
    public SingleCommonResponse<ScheduleResponse> getCurrentSchedule(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) {
        return CommonResponse.ok(queryCurrentScheduleUseCase.execute());
    }

    @GetMapping
    public SingleCommonResponse<ScheduleResponse> getSchedule(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestParam(name = "admission-year") Integer admissionYear
    ) {
        return CommonResponse.ok(queryScheduleUseCase.execute(admissionYear));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SingleCommonResponse<IdResponse> createSchedule(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid ScheduleRequest request
    ) {
        return CommonResponse.ok(createScheduleUseCase.execute(user, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/current")
    public void updateCurrentSchedule(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid ScheduleRequest request
    ) {
        updateScheduleUseCase.execute(user, request);
    }
}
