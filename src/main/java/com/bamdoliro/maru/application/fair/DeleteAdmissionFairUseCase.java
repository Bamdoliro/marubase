package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class DeleteAdmissionFairUseCase {

    private final FairRepository fairRepository;

    public void execute(Long fairId) {
        fairRepository.deleteById(fairId);
    }
}
