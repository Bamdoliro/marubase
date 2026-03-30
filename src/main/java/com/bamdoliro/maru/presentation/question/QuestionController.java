package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.application.question.QueryQuestionListUseCase;
import com.bamdoliro.maru.application.question.QueryQuestionUseCase;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/questions")
@RestController
public class QuestionController {

    private final QueryQuestionListUseCase queryQuestionListUseCase;
    private final QueryQuestionUseCase queryQuestionUseCase;

    @GetMapping
    public ListCommonResponse<QuestionResponse> queryQuestionList(
            @RequestParam(name = "category", required = false) QuestionCategory category
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

}
