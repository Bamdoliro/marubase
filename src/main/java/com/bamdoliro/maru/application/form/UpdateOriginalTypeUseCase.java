package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class UpdateOriginalTypeUseCase {

    private final FormRepository formRepository;

    @Transactional
    public void execute() {
        List<Form> formList = formRepository.findAll();
        formList.forEach(form -> form.setOriginalType(form.getType()));
    }
}
