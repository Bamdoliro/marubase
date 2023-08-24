package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.response.FairDetailResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryFairDetailUseCase {

    private final FairRepository fairRepository;
    private final AttendeeRepository attendeeRepository;

    public FairDetailResponse execute(Long fairId) {
        Fair fair = getFair(fairId);
        return new FairDetailResponse(fair, attendeeRepository);
    }

    private Fair getFair(Long fairId) {
        return fairRepository.findFairAndAttendeeById(fairId)
                .orElseThrow(FairNotFoundException::new);
    }
}
