package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairStatus;
import com.bamdoliro.maru.domain.fair.exception.HeadcountExceededException;
import com.bamdoliro.maru.domain.fair.exception.NotApplicationPeriodException;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@UseCase
public class AttendAdmissionFairUseCase {

    private final FairFacade fairFacade;
    private final AttendeeRepository attendeeRepository;
    private final SendMessageService sendMessageService;

    @Transactional
    public void execute(Long fairId, AttendAdmissionFairRequest request) {
        Fair fair = fairFacade.getFair(fairId);
        validateFairApplicationPeriod(fair);
        validateFairCapacity(fair, request.getHeadcount());

        Attendee attendee = attendeeRepository.save(
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

        sendMessage(fair, attendee);
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

    private void sendMessage(Fair fair, Attendee attendee) {
        String message = String.format("""
                안녕하세요, %s 님.
                부산소프트웨어마이스터고 입학설명회 신청이 완료되었습니다.

                일시: %s
                장소: %s
                신청 인원수: %d명

                시간에 맞춰 참석해 주시길 바랍니다.""",
                attendee.getName(),
                fair.getStart().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")),
                fair.getPlace(),
                attendee.getHeadcount()
                );

        sendMessageService.execute(attendee.getPhoneNumber(), message, "[입학설명회 신청 완료]");
    }
}
