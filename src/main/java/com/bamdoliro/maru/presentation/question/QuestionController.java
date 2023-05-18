package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.application.question.CreateQuestionUseCase;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/question")
@RestController
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestion(@RequestBody @Valid CreateQuestionRequest request) {
        createQuestionUseCase.execute(request);
    }
}
