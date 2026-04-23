package com.bamdoliro.maru.infrastructure.persistence.log;

import com.bamdoliro.maru.domain.log.AdminFormStatusChangeLog;
import org.springframework.data.repository.CrudRepository;

public interface AdminFormStatusChangeLogRepository extends CrudRepository<AdminFormStatusChangeLog, Long> {
}
