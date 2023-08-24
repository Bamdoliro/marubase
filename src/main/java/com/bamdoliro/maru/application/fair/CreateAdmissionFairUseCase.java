package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreateAdmissionFairUseCase {

    private final FairRepository fairRepository;

    @Transactional
    public void execute(CreateFairRequest request) {
        fairRepository.save(
                Fair.builder()
                        .start(request.getStart())
                        .capacity(request.getCapacity())
                        .place(request.getPlace())
                        .type(request.getType())
                        .applicationStartDate(request.getApplicationStartDate())
                        .applicationEndDate(request.getApplicationEndDate())
                        .build()
        );
    }
}
