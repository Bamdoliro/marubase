package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairStatus;
import com.bamdoliro.maru.domain.fair.exception.HeadcountExceededException;
import com.bamdoliro.maru.domain.fair.exception.NotApplicationPeriodException;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class AttendAdmissionFairUseCase {

    private final FairFacade fairFacade;
    private final AttendeeRepository attendeeRepository;

    @Transactional
    public void execute(Long fairId, AttendAdmissionFairRequest request) {
        Fair fair = fairFacade.getFair(fairId);
        validateFairApplicationPeriod(fair);
        validateFairCapacity(fair, request.getHeadcount());

        attendeeRepository.save(
                Attendee.builder()
                        .schoolName(request.getSchoolName())
                        .name(request.getName())
                        .type(request.getType())
                        .phoneNumber(request.getPhoneNumber())
                        .headcount(request.getHeadcount())
                        .question(request.getQuestion())
                        .fair(fair)
                        .build()
        );
    }

    private void validateFairCapacity(Fair fair, Integer headcount) {
        if (fair.getHeadcount(attendeeRepository) + headcount > fair.getCapacity()) {
            throw new HeadcountExceededException();
        }
    }

    private void validateFairApplicationPeriod(Fair fair) {
        if (!fair.getStatus().equals(FairStatus.APPLICATION_IN_PROGRESS)) {
            throw new NotApplicationPeriodException();
        }
    }
}
