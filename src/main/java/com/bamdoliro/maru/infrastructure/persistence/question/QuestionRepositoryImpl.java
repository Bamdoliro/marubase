package com.bamdoliro.maru.infrastructure.persistence.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.bamdoliro.maru.domain.question.domain.QQuestion.question;


@RequiredArgsConstructor
@Repository
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Question> findByCategory(QuestionCategory category) {
        return queryFactory
                .selectFrom(question)
                .where(eqCategory(category))
                .orderBy(question.id.desc())
                .fetch();
    }

    private BooleanExpression eqCategory(QuestionCategory category) {
        if (Objects.isNull(category)) {
            return null;
        }

        return question.category.eq(category);
    }
}
