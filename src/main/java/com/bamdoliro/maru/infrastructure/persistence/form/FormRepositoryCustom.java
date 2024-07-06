package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;

import java.util.List;

public interface FormRepositoryCustom {

    List<Form> findByStatus(FormStatus status);
    List<Form> findReceivedSpecialForm();
    List<Form> findReceivedRegularOrSupernumeraryForm();
    List<Form> findFirstRoundForm();
    List<Form> findMeisterTalentFirstRoundForm();
    List<Form> findNotExistsMeisterTalentFirstRoundForm();
    List<Form> findChangedToRegularFirstRoundForm();
    List<Form> findSecondRoundForm();
    List<Form> findByFormIdList(List<Long> idList);
    List<FormUrlVo> findFormUrlByFormIdList(List<Long> idList);
}
