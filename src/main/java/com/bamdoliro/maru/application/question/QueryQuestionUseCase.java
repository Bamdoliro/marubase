package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryQuestionUseCase {

    private final QuestionFacade questionFacade;

    public QuestionResponse execute(Long id) {
        return new QuestionResponse(
                questionFacade.getQuestion(id)
        );
    }
}
