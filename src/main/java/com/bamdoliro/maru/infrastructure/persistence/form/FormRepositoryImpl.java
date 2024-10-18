package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    @Override
    public List<Form> findByType(FormType type) {
        return queryFactory
                .selectFrom(form)
                .where(eqType(type))
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    @Override
    public List<Form> findByCategory(FormType.Category category) {
        List<FormType> matchingFormTypes = getFormTypesByCategory(category);

        return queryFactory
                .selectFrom(form)
                .where(form.type.in(matchingFormTypes))
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    @Override
    public List<Form> findByOriginalCategory(FormType.Category category) {
        List<FormType> matchingFormTypes = getFormTypesByCategory(category);

        return queryFactory
                .selectFrom(form)
                .where(form.originalType.in(matchingFormTypes))
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    private BooleanExpression eqStatus(FormStatus status) {
        if (Objects.isNull(status)) {
            return null;
        }

        return form.status.eq(status);
    }

    private BooleanExpression eqType(FormType type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return form.type.eq(type);
    }

    private List<FormType> getFormTypesByCategory(FormType.Category category) {
        return Stream.of(FormType.values())
                .filter(formType -> formType.categoryEquals(category))
                .toList();
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
                .orderBy(form.score.firstRoundScore.desc())
                .fetch();
    }

    @Override
    public List<Form> findReceivedRegularForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.RECEIVED)
                                .and(
                                        form.type.eq(FormType.REGULAR)
                                                .or(form.changedToRegular.isTrue())
                                )
                )
                .orderBy(form.score.firstRoundScore.desc())
                .fetch();
    }

    @Override
    public List<Form> findReceivedSupernumeraryForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.RECEIVED)
                                .and(
                                        form.type.eq(FormType.SPECIAL_ADMISSION)
                                                .or(form.type.eq(FormType.NATIONAL_VETERANS_EDUCATION))
                                )
                )
                .orderBy(form.score.firstRoundScore.desc())
                .fetch();
    }

    @Override
    public List<Form> findFirstPassedSpecialForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.FIRST_PASSED)
                                .and(
                                        form.type.eq(FormType.REGULAR).not()
                                                .and(form.type.eq(FormType.NATIONAL_VETERANS_EDUCATION).not())
                                                .and(form.type.eq(FormType.SPECIAL_ADMISSION).not())
                                )
                )
                .orderBy(form.score.totalScore.desc(),
                        form.score.subjectGradeScore.desc(),
                        form.score.depthInterviewScore.desc(),
                        form.score.ncsScore.desc(),
                        form.score.thirdGradeFirstSemesterSubjectGradeScore.desc().nullsLast(),
                        form.score.attendanceScore.desc(),
                        form.score.volunteerScore.desc()
                )
                .fetch();
    }

    @Override
    public List<Form> findFirstPassedRegularForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.FIRST_PASSED)
                                .and(
                                        form.type.eq(FormType.REGULAR)
                                                .or(form.changedToRegular.isTrue())
                                )
                )
                .orderBy(form.score.totalScore.desc(),
                        form.score.subjectGradeScore.desc(),
                        form.score.depthInterviewScore.desc(),
                        form.score.ncsScore.desc(),
                        form.score.thirdGradeFirstSemesterSubjectGradeScore.desc().nullsLast(),
                        form.score.attendanceScore.desc(),
                        form.score.volunteerScore.desc()
                )
                .fetch();
    }

    @Override
    public List<Form> findFirstPassedSupernumeraryForm() {
        return queryFactory
                .selectFrom(form)
                .where(
                        form.status.eq(FormStatus.FIRST_PASSED)
                                .and(
                                        form.type.eq(FormType.SPECIAL_ADMISSION)
                                                .or(form.type.eq(FormType.NATIONAL_VETERANS_EDUCATION))
                                )
                )
                .orderBy(form.score.totalScore.desc(),
                        form.score.subjectGradeScore.desc(),
                        form.score.depthInterviewScore.desc(),
                        form.score.ncsScore.desc(),
                        form.score.thirdGradeFirstSemesterSubjectGradeScore.desc().nullsLast(),
                        form.score.attendanceScore.desc(),
                        form.score.volunteerScore.desc()
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
    public List<Form> findMeisterTalentFirstRoundForm() {
        return queryFactory
                .selectFrom(form)
                .where(form.status.eq(FormStatus.FIRST_PASSED)
                        .and(form.type.eq(FormType.MEISTER_TALENT))
                )
                .fetch();
    }

    @Override
    public List<Form> findNotExistsMeisterTalentAndChangedToRegularFirstRoundForm() {
        return queryFactory
                .selectFrom(form)
                .where(form.status.eq(FormStatus.FIRST_PASSED)
                        .and(form.type.ne(FormType.MEISTER_TALENT)
                                .and(form.changedToRegular.isFalse()))
                )
                .fetch();
    }

    @Override
    public List<Form> findChangedToRegularFirstRoundForm() {
        return queryFactory
                .selectFrom(form)
                .where(form.status.eq(FormStatus.FIRST_PASSED)
                        .and(form.type.eq(FormType.REGULAR)
                                .and(form.changedToRegular.isTrue()))
                )
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
                        form.user.uuid
                ))
                .from(form)
                .where(form.id.in(idList))
                .orderBy(form.examinationNumber.asc())
                .fetch();
    }

    @Override
    public List<NumberOfApplicantsVo> findTypeAndCountGroupByType() {
        return queryFactory
                .select(new QNumberOfApplicantsVo(
                        form.type,
                        form.count()
                ))
                .from(form)
                .groupBy(form.type)
                .fetch();
    }

    @Override
    public List<NumberOfApplicantsVo> findOriginalTypeAndCountGroupByType() {
        return queryFactory
                .select(new QNumberOfApplicantsVo(
                        form.originalType,
                        form.count()
                ))
                .from(form)
                .groupBy(form.originalType)
                .fetch();
    }

    @Override
    public List<GradeVo> findGradeGroupByTypeAndStatus(List<FormStatus> round) {
        return queryFactory
                .select(new QGradeVo(
                        form.type,
                        form.score.firstRoundScore.max(),
                        form.score.firstRoundScore.min(),
                        form.score.firstRoundScore.avg(),
                        form.score.totalScore.max(),
                        form.score.totalScore.min(),
                        form.score.totalScore.avg()
                ))
                .from(form)
                .where(form.status.in(round))
                .groupBy(form.type)
                .fetch();
    }

    @Override
    public List<SchoolStatusVo> findSchoolByAddress(List<FormStatus> round, String keyword) {
        return queryFactory
                .select(new QSchoolStatusVo(
                        form.applicant.name,
                        form.education.school.name,
                        form.education.school.address
                ))
                .from(form)
                .where(form.education.school.address.contains(keyword)
                        .or(form.education.school.location.eq(keyword))
                        .and(form.status.in(round)))
                .fetch();
    }

    @Override
    public List<SchoolStatusVo> findNotBusanSchool(List<FormStatus> round) {
        return queryFactory
                .select(new QSchoolStatusVo(
                        form.applicant.name,
                        form.education.school.name,
                        form.education.school.address
                ))
                .from(form)
                .where(form.education.school.location.eq("부산광역시").not())
                .orderBy(form.applicant.name.asc())
                .fetch();
    }
}