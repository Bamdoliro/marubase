package com.bamdoliro.maru.infrastructure.persistence.log;

import com.bamdoliro.maru.domain.log.AdminFromViewLog;
import org.springframework.data.repository.CrudRepository;

public interface AdminFormViewLogRepository extends CrudRepository<AdminFromViewLog, Long> {
}
