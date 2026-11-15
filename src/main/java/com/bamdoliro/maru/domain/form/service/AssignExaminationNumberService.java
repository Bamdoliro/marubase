package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.ExaminationNumberRangeExceededException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AssignExaminationNumberService {

    private static final long RANGE_SIZE = 1000L;

    private final FormRepository formRepository;

    public void execute(Form form) {
        Long examinationNumber = getNextExaminationNumber(form.getType(), formRepository.findAllExaminationNumber());
        form.assignExaminationNumber(examinationNumber);
    }

    // 여러 원서에 한 번에 번호를 부여한다. 기존 번호 조회(복호화)를 한 번만 하기 위해 사용한다.
    public void executeAll(List<Form> forms) {
        List<Long> examinationNumbers = new ArrayList<>(formRepository.findAllExaminationNumber());
        for (Form form : forms) {
            Long examinationNumber = getNextExaminationNumber(form.getType(), examinationNumbers);
            if (examinationNumber >= getStartNumber(form.getType()) + RANGE_SIZE) {
                throw new ExaminationNumberRangeExceededException();
            }
            form.assignExaminationNumber(examinationNumber);
            examinationNumbers.add(examinationNumber);
        }
    }

    private Long getNextExaminationNumber(FormType type, List<Long> examinationNumbers) {
        Long startNumber = getStartNumber(type);
        return examinationNumbers.stream()
                .filter(num -> num >= startNumber && num <= startNumber + 1000)
                .max(Long::compareTo)
                .orElse(startNumber) + 1;
    }

    private Long getStartNumber(FormType type) {
        return switch (type.getCategory()) {
            case REGULAR -> 1000L;
            case MEISTER_TALENT -> 2000L;
            case SOCIAL_INTEGRATION -> 3000L;
            case SUPERNUMERARY -> 4000L;
            default -> 0L;
        };
    }
}
