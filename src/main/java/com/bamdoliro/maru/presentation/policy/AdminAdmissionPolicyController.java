package com.bamdoliro.maru.presentation.policy;

import com.bamdoliro.maru.application.policy.AdmissionPolicyService;
import com.bamdoliro.maru.presentation.policy.dto.PolicyHistoryResponse;
import com.bamdoliro.maru.presentation.policy.dto.PolicyRequest;
import com.bamdoliro.maru.presentation.policy.dto.PolicyResponse;
import com.bamdoliro.maru.shared.auth.AuthenticatedUser;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/admission-policies")
@RestController
public class AdminAdmissionPolicyController {
    private final AdmissionPolicyService policyService;

    @GetMapping("/current")
    public SingleCommonResponse<PolicyResponse> current(
            @AuthenticationPrincipal(authority = Authority.ADMIN) AuthenticatedUser user
    ) {
        return CommonResponse.ok(policyService.current());
    }

    @GetMapping
    public SingleCommonResponse<PolicyResponse> forYear(
            @AuthenticationPrincipal(authority = Authority.ADMIN) AuthenticatedUser user,
            @RequestParam(name = "admission-year") Integer year
    ) {
        return CommonResponse.ok(policyService.forYear(year));
    }

    @PutMapping("/current")
    public SingleCommonResponse<PolicyResponse> update(
            @AuthenticationPrincipal(authority = Authority.ADMIN) AuthenticatedUser user,
            @RequestBody @Valid PolicyRequest request
    ) {
        return CommonResponse.ok(policyService.update(user.id(), request));
    }

    @GetMapping("/history")
    public ListCommonResponse<PolicyHistoryResponse> history(
            @AuthenticationPrincipal(authority = Authority.ADMIN) AuthenticatedUser user,
            @RequestParam(name = "admission-year") Integer year
    ) {
        return CommonResponse.ok(policyService.history(year));
    }
}
