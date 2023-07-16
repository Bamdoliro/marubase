package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AssignExaminationNumberService {

    private final FormRepository formRepository;

    public void execute(Form form) {
        Long examinationNumber = getNextExaminationNumber(form.getType());
        form.assignExaminationNumber(examinationNumber);
    }

    private Long getNextExaminationNumber(FormType type) {
        Long startNumber = getStartNumber(type);
        return formRepository.findMaxExaminationNumber(startNumber, startNumber+1000)
                .orElse(startNumber)+1;
    }

    private Long getStartNumber(FormType type) {
        return switch (type.getMainCategory()) {
            case REGULAR -> 2000L;
            case SPECIAL -> 3000L;
            case SUPERNUMERARY -> 4000L;
            default -> 0L;
        };
    }
}
