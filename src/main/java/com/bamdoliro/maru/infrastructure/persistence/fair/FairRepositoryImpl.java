package com.bamdoliro.maru.infrastructure.persistence.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.bamdoliro.maru.domain.fair.domain.QFair.fair;


@RequiredArgsConstructor
@Repository
public class FairRepositoryImpl implements FairRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Fair> findByType(FairType type) {
        return queryFactory
                .selectFrom(fair)
                .where(
                        eqType(type)
                )
                .orderBy(fair.start.asc())
                .fetch();
    }

    private BooleanExpression eqType(FairType type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return fair.type.eq(type);
    }

    @Override
    public Optional<Fair> findFairAndAttendeeById(Long fairId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(fair)
                        .leftJoin(fair.attendeeList).fetchJoin()
                        .where(fair.id.eq(fairId))
                        .fetchOne()
        );
    }
}
