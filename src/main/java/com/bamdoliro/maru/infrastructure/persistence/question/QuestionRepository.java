package com.bamdoliro.maru.infrastructure.persistence.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {

}
