package com.bamdoliro.maru.infrastructure.persistence.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;

import java.util.List;

public interface QuestionRepositoryCustom {

    List<Question> findByCategory(QuestionCategory category);
}
