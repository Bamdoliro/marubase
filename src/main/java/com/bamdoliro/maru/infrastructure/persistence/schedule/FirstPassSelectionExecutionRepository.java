package com.bamdoliro.maru.infrastructure.persistence.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.domain.FirstPassSelectionExecution;
import org.springframework.data.repository.CrudRepository;

public interface FirstPassSelectionExecutionRepository
        extends CrudRepository<FirstPassSelectionExecution, Long> {

    boolean existsBySchedule(AdmissionSchedule schedule);
}
