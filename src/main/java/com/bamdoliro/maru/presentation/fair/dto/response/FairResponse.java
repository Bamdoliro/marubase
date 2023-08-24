package com.bamdoliro.maru.presentation.fair.dto.response;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairStatus;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FairResponse {

    private LocalDateTime start;
    private String place;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private FairStatus status;

    public FairResponse(Fair fair, AttendeeRepository attendeeRepository) {
        this.start = fair.getStart();
        this.place = fair.getPlace();
        this.applicationStartDate = fair.getApplicationStartDate();
        this.applicationEndDate = fair.getApplicationEndDate();
        this.status = fair.getStatus(attendeeRepository);
    }
}
