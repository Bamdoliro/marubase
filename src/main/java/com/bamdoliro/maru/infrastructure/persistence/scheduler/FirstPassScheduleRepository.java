package com.bamdoliro.maru.infrastructure.persistence.scheduler;

import com.bamdoliro.maru.domain.scheduler.domain.FirstPassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirstPassScheduleRepository extends JpaRepository<FirstPassSchedule, Long> {
}
