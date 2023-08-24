package com.bamdoliro.maru.presentation.fair.dto.response;

import com.bamdoliro.maru.domain.fair.domain.Fair;
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

    public FairResponse(Fair fair) {
        this.start = fair.getStart();
        this.place = fair.getPlace();
        this.applicationStartDate = fair.getApplicationStartDate();
        this.applicationEndDate = fair.getApplicationEndDate();
    }
}
