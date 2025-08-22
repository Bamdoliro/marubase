package com.bamdoliro.maru.infrastructure.persistence.scheduler;

import com.bamdoliro.maru.domain.scheduler.domain.FirstPassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FirstPassScheduleRepository extends JpaRepository<FirstPassSchedule, Long> {

    Optional<FirstPassSchedule> findTopByOrderByCreatedAtDesc();

}
