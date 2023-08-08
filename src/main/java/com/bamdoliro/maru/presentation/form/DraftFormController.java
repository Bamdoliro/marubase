package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.DraftFormUseCase;
import com.bamdoliro.maru.application.form.QueryDraftFormUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/form/draft")
@RestController
public class DraftFormController {

    private final DraftFormUseCase draftFormUseCase;
    private final QueryDraftFormUseCase queryDraftFormUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void draftForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody SubmitFormRequest request
    ) {
        draftFormUseCase.execute(user, request);
    }

    @GetMapping
    public SingleCommonResponse<SubmitFormRequest> getDraftForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return CommonResponse.ok(
                queryDraftFormUseCase.execute(user)
        );
    }
}
