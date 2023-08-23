package com.bamdoliro.maru.infrastructure.persistence.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long>, QuestionRepositoryCustom {
}
