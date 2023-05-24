package com.bamdoliro.maru.domain.question.exception;

import com.bamdoliro.maru.domain.question.exception.error.QuestionErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;
import org.springframework.http.HttpStatus;

public class QuestionIdNotFoundException extends MaruException {
    public QuestionIdNotFoundException() {
        super(QuestionErrorProperty.QUESTION_NOT_FOUND);
    }
}
