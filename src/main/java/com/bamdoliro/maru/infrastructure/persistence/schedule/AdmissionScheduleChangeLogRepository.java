package com.bamdoliro.maru.infrastructure.persistence.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionScheduleChangeLog;
import org.springframework.data.repository.CrudRepository;

public interface AdmissionScheduleChangeLogRepository
        extends CrudRepository<AdmissionScheduleChangeLog, Long> {
}
