package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.application.question.CreateQuestionUseCase;
import com.bamdoliro.maru.application.question.UpdateQuestionUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/question")
@RestController
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid CreateQuestionRequest request
    ) {
        createQuestionUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable Long id,
            @RequestBody UpdateQuestionRequest request
    ) {
        updateQuestionUseCase.execute(id, request);
    }
}
