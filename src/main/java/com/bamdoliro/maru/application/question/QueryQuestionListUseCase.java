package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QueryQuestionListUseCase {

    private final QuestionRepository questionRepository;

    public List<QuestionResponse> execute(QuestionCategory category) {
        return questionRepository.findByCategory(category)
                .stream()
                .map(QuestionResponse::new)
                .toList();
    }
}
