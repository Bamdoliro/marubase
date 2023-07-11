package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.QueryQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@UseCase
public class QueryAllQuestionUseCase {

    private final QuestionRepository questionRepository;

    public void queryQuestions(QueryQuestionRequest queryQuestionRequest) {
        Pageable sortPageable = PageRequest.of(queryQuestionRequest.getPage(), queryQuestionRequest.getSize(), Sort.by("question_id").descending());
        questionRepository.findAll(sortPageable);
    }
}
