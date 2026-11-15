package com.bamdoliro.maru.infrastructure.persistence.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionScheduleChangeLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AdmissionScheduleChangeLogRepository
        extends CrudRepository<AdmissionScheduleChangeLog, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from AdmissionScheduleChangeLog l where l.userId in (select u.id from User u where u.isTestData = true)")
    int deleteAllOfTestData();
}
