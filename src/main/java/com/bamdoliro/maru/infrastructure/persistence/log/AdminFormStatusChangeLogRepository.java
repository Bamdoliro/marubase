package com.bamdoliro.maru.infrastructure.persistence.log;

import com.bamdoliro.maru.domain.log.AdminFormStatusChangeLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AdminFormStatusChangeLogRepository extends CrudRepository<AdminFormStatusChangeLog, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from AdminFormStatusChangeLog l where l.user.id in (select u.id from User u where u.isTestData = true) or l.form.id in (select f.id from Form f where f.user.isTestData = true)")
    int deleteAllOfTestData();
}
