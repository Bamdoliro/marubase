package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QuestionFacade {

    private final QuestionRepository questionRepository;

    public Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(QuestionNotFoundException::new);
    }
}
