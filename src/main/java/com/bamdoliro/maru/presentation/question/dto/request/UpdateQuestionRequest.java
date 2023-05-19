package com.bamdoliro.maru.presentation.question.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuestionRequest {
    private String title;
    private String content;

}
