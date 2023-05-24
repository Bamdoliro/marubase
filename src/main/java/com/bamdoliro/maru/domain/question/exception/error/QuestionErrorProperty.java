package com.bamdoliro.maru.domain.question.exception.error;

import com.bamdoliro.maru.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuestionErrorProperty implements ErrorProperty {
    QUESTION_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 질문을 찾을수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
