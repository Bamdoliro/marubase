package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.application.question.CreateQuestionUseCase;
import com.bamdoliro.maru.application.question.DeleteQuestionUseCase;
import com.bamdoliro.maru.application.question.UpdateQuestionUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.IdResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/questions")
public class AdminQuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SingleCommonResponse<IdResponse> createQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid CreateQuestionRequest request
    ) {
        return CommonResponse.ok(createQuestionUseCase.execute(request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{question-id}")
    public void updateQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "question-id") Long questionId,
            @RequestBody @Valid UpdateQuestionRequest request
    ) {
        updateQuestionUseCase.execute(questionId, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{question-id}")
    public void deleteQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "question-id") Long questionId
    ) {
        deleteQuestionUseCase.execute(questionId);
    }

}
