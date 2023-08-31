package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FormFacade {

    private final FormRepository formRepository;

    public Form getForm(Long id) {
        return formRepository.findById(id)
                .orElseThrow(FormNotFoundException::new);
    }

    public Form getForm(User user) {
        return formRepository.findByUser(user)
                .orElseThrow(FormNotFoundException::new);
    }

    public Form getFormByExaminationNumber(Long examinationNumber) {
        return formRepository.findByExaminationNumber(examinationNumber)
                .orElseThrow(FormNotFoundException::new);
    }

    public List<Form> getSortedFormList(FormStatus status) {
        return formRepository.findByStatus(status)
                .stream()
                .sorted(
                        getFormComparator()
                                .thenComparing(Form::getExaminationNumber)
                )
                .toList();
    }

    public Comparator<Form> getFormComparator() {
        return Comparator
                .comparing(this::getOrder)
                .thenComparing(Form::getChangedToRegular);
    }

    private Integer getOrder(Form form) {
        return getOrderMap().get(form.getType().getCategory());
    }

    private Map<FormType.Category, Integer> getOrderMap() {
        return Map.of(
                FormType.Category.REGULAR, 1,
                FormType.Category.MEISTER_TALENT, 2,
                FormType.Category.SOCIAL_INTEGRATION, 3,
                FormType.Category.SUPERNUMERARY, 4
        );
    }
}
