package com.bamdoliro.maru.infrastructure.persistence.log;

import com.bamdoliro.maru.domain.log.AdminLookupFormLog;
import org.springframework.data.repository.CrudRepository;

public interface AdminLookupFormLogRepository extends CrudRepository<AdminLookupFormLog, Long> {
}
