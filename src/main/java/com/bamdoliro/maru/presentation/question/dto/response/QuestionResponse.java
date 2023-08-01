package com.bamdoliro.maru.presentation.question.dto.response;

import com.bamdoliro.maru.domain.question.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionResponse {

    private String title;
    private String content;

    public QuestionResponse(Question question) {
        this.title = question.getTitle();
        this.content = question.getContent();
    }
}
