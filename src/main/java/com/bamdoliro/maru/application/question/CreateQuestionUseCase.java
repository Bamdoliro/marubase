package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.response.IdResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CreateQuestionUseCase {

    private final QuestionRepository questionRepository;

    public IdResponse execute(CreateQuestionRequest request) {
        Question question = questionRepository.save(
                new Question(request.getTitle(), request.getContent(), request.getCategory()));

        return new IdResponse(question);
    }
}
