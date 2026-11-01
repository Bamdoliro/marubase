package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.service.ExaminationNumberLock;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// H2 테스트용 구현체. H2에는 pg_advisory_xact_lock이 없다.
@Profile("test")
@Component
public class NoOpExaminationNumberLock implements ExaminationNumberLock {

    @Override
    public void lock() {
    }
}
