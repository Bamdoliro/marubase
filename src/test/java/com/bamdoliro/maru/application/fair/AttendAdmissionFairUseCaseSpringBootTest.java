package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AttendAdmissionFairUseCaseSpringBootTest {

    @Autowired
    private AttendAdmissionFairUseCase attendAdmissionFairUseCase;

    @Autowired
    private FairRepository fairRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @MockitoBean
    private SendMessageService sendMessageService;

    @Test
    void 입학설명회_참석_신청을_한다() {
        Fair fair = fairRepository.save(FairFixture.createFair());
        AttendAdmissionFairRequest request = new AttendAdmissionFairRequest(
                "비전중학교",
                "김한울",
                "학생",
                "01012345678",
                2,
                "급식 존맛?"
        );

        attendAdmissionFairUseCase.execute(fair.getId(), request);

        assertThat(attendeeRepository.existsByFairAndPhoneNumber(fair, request.getPhoneNumber())).isTrue();
        assertThat(attendeeRepository.sumHeadcountByFair(fair)).isEqualTo(request.getHeadcount().longValue());
        verify(sendMessageService).execute(
                eq(request.getPhoneNumber()),
                contains(request.getName()),
                eq("[입학설명회 신청 완료]")
        );
    }
}
