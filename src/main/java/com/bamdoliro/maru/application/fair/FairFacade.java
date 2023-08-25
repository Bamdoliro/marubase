package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FairFacade {

    private final FairRepository fairRepository;

    public Fair getFair(Long id) {
        return fairRepository.findById(id)
                .orElseThrow(FairNotFoundException::new);
    }

    public Fair getFairDetail(Long id) {
        return fairRepository.findFairAndAttendeeById(id)
                .orElseThrow(FairNotFoundException::new);
    }
}
