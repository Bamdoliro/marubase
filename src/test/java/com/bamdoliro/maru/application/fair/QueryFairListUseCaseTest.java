package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.presentation.fair.dto.response.FairResponse;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryFairListUseCaseTest {

    @InjectMocks
    private QueryFairListUseCase queryFairListUseCase;

    @Mock
    private FairRepository fairRepository;

    @Mock
    private AttendeeRepository attendeeRepository;


    @Test
    void 입학설명회_일정을_가져온다() {
        // given
        List<Fair> fairList = FairFixture.createFairList();
        given(fairRepository.findByType(FairType.STUDENT_AND_PARENT)).willReturn(fairList);
        given(attendeeRepository.countByFair(any(Fair.class))).willReturn(20);

        // when
        List<FairResponse> response = queryFairListUseCase.execute(FairType.STUDENT_AND_PARENT);

        // then
        assertEquals(fairList.size(), response.size());

        verify(fairRepository, times(1)).findByType(FairType.STUDENT_AND_PARENT);
        verify(attendeeRepository, times(response.size())).countByFair(any(Fair.class));
    }

}