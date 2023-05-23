package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreateQuestionUseCase {

    private final QuestionRepository questionRepository;

    @Transactional
    public void execute(CreateQuestionRequest request) {
        questionRepository.save(
                new Question(request.getTitle(), request.getContent()));
    }
}