package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.presentation.fair.dto.request.UpdateFairRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdateAdmissionFairUseCase {

    private final FairFacade fairFacade;

    @Transactional
    public void execute(Long id, UpdateFairRequest request) {
        Fair fair = fairFacade.getFair(id);
        fair.update(request.getStart(), request.getCapacity(), request.getPlace(), request.getType(), request.getApplicationStartDate(), request.getApplicationEndDate());
    }
}
