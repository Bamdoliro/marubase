package com.bamdoliro.maru.domain.question.exception;

import com.bamdoliro.maru.domain.question.exception.error.QuestionErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class QuestionNotFoundException extends MaruException {

    public QuestionNotFoundException() {
        super(QuestionErrorProperty.QUESTION_NOT_FOUND);
    }
}
