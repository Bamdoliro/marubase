package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.presentation.fair.dto.request.UpdateFairRequest;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateAdmissionFairUseCaseTest {

    @InjectMocks
    private UpdateAdmissionFairUseCase updateAdmissionFairUseCase;

    @Mock
    private FairFacade fairFacade;

    @Test
    void 입학설명회를_수정한다() {
        // given
        Fair fair = FairFixture.createFair();
        LocalDateTime time = LocalDateTime.of(2026, 4, 13, 8, 19, 41);
        LocalDate date = LocalDate.of(3939, 3, 9);
        UpdateFairRequest request = new UpdateFairRequest(time, 100, "부산소프트웨어마이스터고등학교 어딘가", FairType.TEACHER, date, date);

        given(fairFacade.getFair(fair.getId())).willReturn(fair);

        // when
        updateAdmissionFairUseCase.execute(fair.getId(), request);

        // then
        verify(fairFacade).getFair(fair.getId());

        assertAll(
                () -> assertEquals(request.getStart(), fair.getStart()),
                () -> assertEquals(request.getCapacity(), fair.getCapacity()),
                () -> assertEquals(request.getPlace(), fair.getPlace()),
                () -> assertEquals(request.getType(), fair.getType()),
                () -> assertEquals(request.getApplicationStartDate(), fair.getApplicationStartDate()),
                () -> assertEquals(request.getApplicationEndDate(), fair.getApplicationEndDate())
        );
    }
}
