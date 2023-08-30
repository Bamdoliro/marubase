package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class DeleteQuestionUseCase {

    private final QuestionRepository questionRepository;

    public void execute(Long questionId) {
        questionRepository.deleteById(questionId);
    }
}
