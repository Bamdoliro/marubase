package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;

import java.util.List;

public interface FormRepositoryCustom {

    List<Form> findByStatus(FormStatus status);
    List<Form> findReceivedSpecialForm();
    List<Form> findReceivedRegularOrSupernumeraryForm();
    List<Form> findFirstRoundForm();
    List<Form> findSecondRoundForm();
}
