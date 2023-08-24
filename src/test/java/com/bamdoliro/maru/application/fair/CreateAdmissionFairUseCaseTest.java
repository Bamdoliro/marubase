package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateAdmissionFairUseCaseTest {

    @InjectMocks
    private CreateAdmissionFairUseCase createAdmissionFairUseCase;

    @Mock
    private FairRepository fairRepository;

    @Test
    void 입학설명회_일정을_만든다() {
        // given
        CreateFairRequest request = FairFixture.createFairRequest();
        Fair fair = FairFixture.createFair();
        given(fairRepository.save(any(Fair.class))).willReturn(fair);
        ArgumentCaptor<Fair> captor = ArgumentCaptor.forClass(Fair.class);

        // when
        createAdmissionFairUseCase.execute(request);

        // then
        verify(fairRepository, times(1)).save(captor.capture());
        Fair savedFair = captor.getValue();
        assertEquals(fair.getStart(), savedFair.getStart());
        assertEquals(fair.getPlace(), savedFair.getPlace());
        assertEquals(fair.getStart().minusDays(3).toLocalDate(), savedFair.getApplicationEndDate());
    }
}