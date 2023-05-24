package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionIdNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionFacade {
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final QuestionRepository questionRepository;

    public Question getQuestion(Long id) {
         return questionRepository.findById(id).orElseThrow(
                QuestionIdNotFoundException::new
        );
    }
}