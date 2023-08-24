package com.bamdoliro.maru.presentation.fair;

import com.bamdoliro.maru.application.fair.AttendAdmissionFairUseCase;
import com.bamdoliro.maru.application.fair.CreateAdmissionFairUseCase;
import com.bamdoliro.maru.application.fair.QueryFairDetailUseCase;
import com.bamdoliro.maru.application.fair.QueryFairListUseCase;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.response.FairDetailResponse;
import com.bamdoliro.maru.presentation.fair.dto.response.FairResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/fair")
@RestController
public class FairController {

    private final CreateAdmissionFairUseCase createAdmissionFairUseCase;
    private final AttendAdmissionFairUseCase attendAdmissionFairUseCase;
    private final QueryFairListUseCase queryFairListUseCase;
    private final QueryFairDetailUseCase queryFairDetailUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createAdmissionFair(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid CreateFairRequest request
    ) {
        createAdmissionFairUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{fair-id}")
    public void attendAdmissionFair(
            @PathVariable(name = "fair-id") Long fairId,
            @RequestBody @Valid AttendAdmissionFairRequest request
    ) {
        attendAdmissionFairUseCase.execute(fairId, request);
    }

    @GetMapping
    public ListCommonResponse<FairResponse> getAdmissionFairList(
            @RequestParam(name = "type", required = false) FairType type
    ) {
        return CommonResponse.ok(
                queryFairListUseCase.execute(type)
        );
    }

    @GetMapping("/{fair-id}")
    public SingleCommonResponse<FairDetailResponse> getFairDetail(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "fair-id") Long fairId
    ) {
        return CommonResponse.ok(
                queryFairDetailUseCase.execute(fairId)
        );
    }
}
