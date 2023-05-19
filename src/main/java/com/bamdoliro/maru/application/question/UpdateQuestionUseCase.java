package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionIdNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdateQuestionUseCase{

    private final QuestionRepository questionRepository;

    @Transactional
    public void execute(Long id, UpdateQuestionRequest request) {

        Question question = questionRepository.findById(id).orElseThrow(
                QuestionIdNotFoundException::new
        );
        question.update(request);

    }


}
