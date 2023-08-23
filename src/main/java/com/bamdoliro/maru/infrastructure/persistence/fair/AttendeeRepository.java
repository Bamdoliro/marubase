package com.bamdoliro.maru.infrastructure.persistence.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import org.springframework.data.repository.CrudRepository;

public interface AttendeeRepository extends CrudRepository<Attendee, Long> {
}
