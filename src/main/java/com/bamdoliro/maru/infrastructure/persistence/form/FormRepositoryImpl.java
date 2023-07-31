package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.bamdoliro.maru.domain.form.domain.QForm.form;

@Repository
@RequiredArgsConstructor
public class FormRepositoryImpl implements FormRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Form> findByStatus(FormStatus status) {
        return queryFactory
                .selectFrom(form)
                .where(eqStatus(status))
                .fetch();
    }

    private BooleanExpression eqStatus(FormStatus status) {
        if (Objects.isNull(status)) {
            return null;
        }

        return form.status.eq(status);
    }
}
