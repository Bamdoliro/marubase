package com.bamdoliro.maru.presentation.policy;

import com.bamdoliro.maru.domain.policy.formula.ScoreFormulaPreview;
import com.bamdoliro.maru.presentation.policy.dto.ScorePreviewRequest;
import com.bamdoliro.maru.shared.auth.AuthenticatedUser;
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

@RequiredArgsConstructor
@RequestMapping("/admin/admission-policies")
@RestController
public class AdminScorePreviewController {
    private final ScoreFormulaPreview preview;

    @PostMapping("/score-preview")
    public SingleCommonResponse<ScoreFormulaPreview.Result> preview(
            @AuthenticationPrincipal(authority = Authority.ADMIN) AuthenticatedUser user,
            @RequestBody @Valid ScorePreviewRequest request
    ) {
        return CommonResponse.ok(preview.execute(request.source(), request.inputs(), request.subjects()));
    }
}
