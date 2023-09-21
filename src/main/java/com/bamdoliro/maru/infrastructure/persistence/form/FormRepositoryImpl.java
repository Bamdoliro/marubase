package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.QFormUrlVo;
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
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    private BooleanExpression eqStatus(FormStatus status) {
        if (Objects.isNull(status)) {
            return null;
        }

        return form.status.eq(status);
    }

    @Override
    public List<Form> findReceivedSpecialForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.RECEIVED)
                                .and(
                                        form.type.eq(FormType.REGULAR).not()
                                                .and(form.type.eq(FormType.NATIONAL_VETERANS_EDUCATION).not())
                                                .and(form.type.eq(FormType.SPECIAL_ADMISSION).not())
                                )
                )
                .orderBy(
                        form.score.firstRoundScore.desc()
                )
                .fetch();
    }

    @Override
    public List<Form> findReceivedRegularOrSupernumeraryForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.RECEIVED)
                                .and(
                                        form.type.eq(FormType.REGULAR)
                                                .or(form.type.eq(FormType.NATIONAL_VETERANS_EDUCATION))
                                                .or(form.type.eq(FormType.SPECIAL_ADMISSION))
                                                .or(form.changedToRegular.isTrue())
                                )
                )
                .orderBy(
                        form.score.firstRoundScore.desc()
                )
                .fetch();
    }

    @Override
    public List<Form> findFirstRoundForm() {
        return queryFactory
                .selectFrom(form)
                .where(form.status.eq(FormStatus.FIRST_PASSED)
                        .or(form.status.eq(FormStatus.FIRST_FAILED))
                )
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    @Override
    public List<Form> findSecondRoundForm() {
        return queryFactory
                .selectFrom(form)
                .where(form.status.eq(FormStatus.FAILED)
                        .or(form.status.eq(FormStatus.PASSED))
                )
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    @Override
    public List<Form> findByFormIdList(List<Long> idList) {
        return queryFactory
                .selectFrom(form)
                .where(form.id.in(idList))
                .orderBy(form.id.asc())
                .fetch();
    }

    @Override
    public List<FormUrlVo> findFormUrlByFormIdList(List<Long> idList) {
        return queryFactory
                .select(new QFormUrlVo(
                        form.examinationNumber,
                        form.applicant.name,
                        form.formUrl
                ))
                .from(form)
                .where(form.id.in(idList))
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }
}
