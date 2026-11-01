package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.service.ExaminationNumberLock;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Profile("!test")
@RequiredArgsConstructor
@Component
public class PostgresExaminationNumberLock implements ExaminationNumberLock {

    // 수험번호 부여 전용 advisory lock 키. 다른 advisory lock과 겹치지 않게 이 값만 사용한다.
    static final long LOCK_KEY = 462_001L;

    private final EntityManager entityManager;

    // 트랜잭션이 끝날 때까지 락을 유지한다. READ COMMITTED 격리 수준을 전제로 한다.
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void lock() {
        entityManager.createNativeQuery("select 1 from pg_advisory_xact_lock(:key)")
                .setParameter("key", LOCK_KEY)
                .getSingleResult();
    }
}
