package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.application.question.CreateQuestionUseCase;
import com.bamdoliro.maru.application.question.DeleteQuestionUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionListUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionUseCase;
import com.bamdoliro.maru.application.question.UpdateQuestionUseCase;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/question")
@RestController
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final QueryQuestionListUseCase queryQuestionListUseCase;
    private final QueryQuestionUseCase queryQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid CreateQuestionRequest request
    ) {
        createQuestionUseCase.execute(request);
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

    @GetMapping
    public ListCommonResponse<QuestionResponse> queryQuestionList(
            @RequestParam QuestionCategory category
    ) {
        return CommonResponse.ok(
                queryQuestionListUseCase.execute(category)
        );
    }

    @GetMapping("/{question-id}")
    public SingleCommonResponse<QuestionResponse> queryQuestion(
            @PathVariable(name = "question-id") Long questionId
    ) {
        return CommonResponse.ok(
                queryQuestionUseCase.execute(questionId)
        );
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
