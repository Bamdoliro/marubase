package com.bamdoliro.maru.presentation.analysis;

import com.bamdoliro.maru.application.analysis.QueryGenderRatioUseCase;
import com.bamdoliro.maru.application.analysis.QueryNumberOfApplicantsUseCase;
import com.bamdoliro.maru.application.analysis.QueryGradeDistributionUseCase;
import com.bamdoliro.maru.application.analysis.QuerySchoolStatusUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.analysis.dto.request.GenderRatioRequest;
import com.bamdoliro.maru.presentation.analysis.dto.request.GradeDistributionRequest;
import com.bamdoliro.maru.presentation.analysis.dto.request.SchoolStatusRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.GenderRatioResponse;
import com.bamdoliro.maru.presentation.analysis.dto.response.NumberOfApplicantsResponse;
import com.bamdoliro.maru.presentation.analysis.dto.response.GradeDistributionResponse;
import com.bamdoliro.maru.presentation.analysis.dto.response.SchoolStatusResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/analysis")
@RestController
public class AnalysisController {
    private final QueryNumberOfApplicantsUseCase queryNumberOfApplicantsUseCase;
    private final QueryGradeDistributionUseCase queryGradeDistributionUseCase;
    private final QueryGenderRatioUseCase queryGenderRatioUseCase;
    private final QuerySchoolStatusUseCase querySchoolStatusUseCase;

    @GetMapping("/number-of-applicants")
    public ListCommonResponse<NumberOfApplicantsResponse> getNumberOfApplicants(
            @AuthenticationPrincipal(authority = Authority.ADMIN)User user,
            @RequestParam String type
    ) {
        return CommonResponse.ok(
                queryNumberOfApplicantsUseCase.execute(type)
        );
    }

    @GetMapping("/grade-distribution")
    public ListCommonResponse<GradeDistributionResponse> getGradeDistribution(
            @AuthenticationPrincipal(authority = Authority.ADMIN)User user,
            @ModelAttribute @Valid GradeDistributionRequest request
    ) {
        return CommonResponse.ok(
                queryGradeDistributionUseCase.execute(request)
        );
    }

    @GetMapping("/gender-ratio")
    public ListCommonResponse<GenderRatioResponse> getGenderRatio(
            @AuthenticationPrincipal(authority = Authority.ADMIN)User user,
            @ModelAttribute @Valid GenderRatioRequest request
    ) {
        return CommonResponse.ok(
                queryGenderRatioUseCase.execute(request)
        );
    }

    @GetMapping("/school-status")
    public ListCommonResponse<SchoolStatusResponse> getSchoolStatus(
            @AuthenticationPrincipal(authority = Authority.ADMIN)User user,
            @ModelAttribute @Valid SchoolStatusRequest request
    ) {
        return CommonResponse.ok(
                querySchoolStatusUseCase.execute(request)
        );
    }
}