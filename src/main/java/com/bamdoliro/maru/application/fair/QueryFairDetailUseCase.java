package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.presentation.fair.dto.response.FairDetailResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryFairDetailUseCase {

    private final FairFacade fairFacade;
    private final AttendeeRepository attendeeRepository;

    public FairDetailResponse execute(Long fairId) {
        Fair fair = fairFacade.getFairDetail(fairId);
        return new FairDetailResponse(fair, attendeeRepository);
    }
}
