package com.bamdoliro.maru.infrastructure.persistence.log;

import com.bamdoliro.maru.domain.log.AdminLoginLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AdminLoginLogRepository extends CrudRepository<AdminLoginLog, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from AdminLoginLog l where l.user.id in (select u.id from User u where u.isTestData = true)")
    int deleteAllOfTestData();
}
