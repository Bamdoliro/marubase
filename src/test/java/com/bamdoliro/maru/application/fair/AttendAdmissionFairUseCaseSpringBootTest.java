package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class AttendAdmissionFairUseCaseSpringBootTest {

    @Autowired
    private AttendAdmissionFairUseCase attendAdmissionFairUseCase;

    @Autowired
    private FairRepository fairRepository;

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
    }
}