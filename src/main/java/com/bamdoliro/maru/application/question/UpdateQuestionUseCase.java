package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionIdNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@RequiredArgsConstructor
@UseCase
public class UpdateQuestionUseCase{

    private final QuestionRepository questionRepository;
    private final QuestionFacade questionFacade;

    @Transactional
    public void execute(Long id, UpdateQuestionRequest request) {

        Question question = questionFacade.getQuestion1(id);
        question.update(request);

    }


}
