package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.NumberOfApplicantsVo;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.GradeVo;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.SchoolStatusVo;

import java.util.List;

public interface FormRepositoryCustom {

    List<Form> findByStatus(FormStatus status);
    List<Form> findByType(FormType type);
    List<Form> findByCategory(FormType.Category category);
    List<Form> findByOriginalCategory(FormType.Category category);
    List<Form> findReceivedSpecialForm();
    List<Form> findReceivedRegularForm();
    List<Form> findReceivedSupernumeraryForm();
    List<Form> findFirstPassedSpecialForm();
    List<Form> findFirstPassedRegularForm();
    List<Form> findFirstPassedSupernumeraryForm();
    List<Form> findFirstRoundForm();
    List<Form> findMeisterTalentFirstRoundForm();
    List<Form> findNotExistsMeisterTalentAndChangedToRegularFirstRoundForm();
    List<Form> findChangedToRegularFirstRoundForm();
    List<Form> findSecondRoundForm();
    List<Form> findByFormIdList(List<Long> idList);
    List<FormUrlVo> findFormUrlByFormIdList(List<Long> idList);
    List<NumberOfApplicantsVo> findTypeAndCountGroupByType();
    List<NumberOfApplicantsVo> findOriginalTypeAndCountGroupByType();
    List<GradeVo> findGradeGroupByTypeAndStatus(List<FormStatus> round);
    List<SchoolStatusVo> findSchoolByAddress(List<FormStatus> round, String keyword);
    List<SchoolStatusVo> findNotBusanSchool(List<FormStatus> round);
}