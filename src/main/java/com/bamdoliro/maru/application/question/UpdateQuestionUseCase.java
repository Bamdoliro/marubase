package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
//@UseCase
@Service
public class UpdateQuestionUseCase{

    private final QuestionRepository questionRepository;

    @Transactional
    public Long execute(Long id, UpdateQuestionRequest request) {

        Question question = questionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Not Found Question")
        );
        return question.updateData(request);

    }


}
