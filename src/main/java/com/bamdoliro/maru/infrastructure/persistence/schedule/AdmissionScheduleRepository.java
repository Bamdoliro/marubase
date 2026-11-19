package com.bamdoliro.maru.infrastructure.persistence.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface AdmissionScheduleRepository extends JpaRepository<AdmissionSchedule, Long> {

    Optional<AdmissionSchedule> findByActiveTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AdmissionSchedule s where s.active = true")
    Optional<AdmissionSchedule> findActiveForUpdate();

    Optional<AdmissionSchedule> findByAdmissionYear(Integer admissionYear);

    boolean existsByAdmissionYear(Integer admissionYear);

    Optional<AdmissionSchedule> findFirstByOrderByAdmissionYearDesc();
}
