package com.bamdoliro.maru.infrastructure.persistence.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendeeRepository extends CrudRepository<Attendee, Long> {

    @Query("select coalesce(sum(attendee.headcount), 0) from Attendee attendee where attendee.fair = :fair")
    long sumHeadcountByFair(@Param("fair") Fair fair);

    boolean existsByFairAndPhoneNumber(Fair fair, String phoneNumber);
}
