package com.bamdoliro.maru.presentation.question.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UpdateQuestionRequest {
    private String title;
    private String content;

}
