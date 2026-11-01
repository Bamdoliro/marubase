package com.bamdoliro.maru.infrastructure.persistence.policy;

import com.bamdoliro.maru.domain.policy.AdmissionPolicyChangeLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionPolicyChangeLogRepository extends JpaRepository<AdmissionPolicyChangeLog, Long> {
    List<AdmissionPolicyChangeLog> findTop50ByScheduleIdOrderByIdDesc(Long scheduleId);
}
