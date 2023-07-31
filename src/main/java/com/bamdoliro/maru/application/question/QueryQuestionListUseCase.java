package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@UseCase
public class QueryQuestionListUseCase {

    private final QuestionRepository questionRepository;

    public void execute(QuestionCategory category, Pageable pageable) {
        questionRepository.findByCategory(category, pageable);
    }
}
