package com.bamdoliro.maru.presentation.question.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Valid
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuestionRequest {
    private String title;
    private String content;

}
