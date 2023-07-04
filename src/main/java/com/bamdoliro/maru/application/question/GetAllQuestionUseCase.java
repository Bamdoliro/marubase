package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@UseCase
public class GetAllQuestionUseCase {

    private final QuestionRepository questionRepository;

    public Page<Question> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }
}
