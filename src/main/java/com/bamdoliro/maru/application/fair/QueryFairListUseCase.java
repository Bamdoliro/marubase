package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.response.FairResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QueryFairListUseCase {

    private final FairRepository fairRepository;

    public List<FairResponse> execute(FairType type) {
        return fairRepository.findByType(type)
                .stream()
                .map(FairResponse::new)
                .toList();
    }
}
