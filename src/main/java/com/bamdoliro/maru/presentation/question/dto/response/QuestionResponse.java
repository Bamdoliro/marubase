package com.bamdoliro.maru.presentation.question.dto.response;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private String title;
    private String content;
    private QuestionCategory category;
    private LocalDateTime createdAt;

    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.category = question.getCategory();
        this.createdAt = question.getUpdatedAt();
    }
}
