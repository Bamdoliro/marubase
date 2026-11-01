package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.ExaminationNumberExhaustedException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AssignExaminationNumberService {

    private static final long RANGE_SIZE = 1000L;

    private final FormRepository formRepository;
    private final ExaminationNumberLock examinationNumberLock;

    // 번호를 저장하는 쓰기(Form 저장)는 반드시 이 메서드를 호출한 트랜잭션 안에서 해야 한다.
    // 락은 트랜잭션 커밋까지 유지되므로, 계산과 저장이 다른 트랜잭션이면 직렬화가 깨진다.
    public void execute(Form form) {
        examinationNumberLock.lock();
        Long examinationNumber = getNextExaminationNumber(form.getType());
        form.assignExaminationNumber(examinationNumber);
    }

    private Long getNextExaminationNumber(FormType type) {
        long startNumber = getStartNumber(type.getCategory());
        long endNumber = startNumber + RANGE_SIZE;
        long lastNumber = formRepository.findAllExaminationNumber().stream()
                .filter(num -> num > startNumber && num < endNumber)
                .max(Long::compareTo)
                .orElse(startNumber);

        if (lastNumber + 1 >= endNumber) {
            log.error("수험번호 구간이 소진되었습니다. 전형: {}, 마지막 번호: {}", type.getCategory(), lastNumber);
            throw new ExaminationNumberExhaustedException();
        }

        return lastNumber + 1;
    }

    static long getStartNumber(FormType.Category category) {
        return switch (category) {
            case REGULAR -> 1000L;
            case MEISTER_TALENT -> 2000L;
            case SOCIAL_INTEGRATION -> 3000L;
            case SUPERNUMERARY -> 4000L;
            default -> throw new IllegalStateException("수험번호 구간이 정의되지 않은 전형입니다: " + category);
        };
    }
}
