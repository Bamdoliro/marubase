package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.presentation.fair.dto.response.FairDetailResponse;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryFairDetailUseCaseTest {

    @InjectMocks
    private QueryFairDetailUseCase queryFairDetailUseCase;

    @Mock
    private FairFacade fairFacade;

    @Mock
    private AttendeeRepository attendeeRepository;

    @Test
    void 입학설명회를_상세히_불러온다() {
        // given
        Fair fair = FairFixture.createFairDetail();
        given(fairFacade.getFairDetail(fair.getId())).willReturn(fair);

        // when
        FairDetailResponse response = queryFairDetailUseCase.execute(fair.getId());

        // then
        assertEquals(fair.getStart(), response.getStart());
        assertEquals(fair.getPlace(), response.getPlace());
        assertEquals(fair.getAttendeeList().size(), response.getAttendeeList().size());

        verify(fairFacade, times(1)).getFairDetail(fair.getId());
    }

    @Test
    void 입학설명회를_상세히_불러올_때_해당_입학설명회가_없으면_에러가_발생한다() {
        // given
        willThrow(new FairNotFoundException()).given(fairFacade).getFairDetail(anyLong());

        // when and then
        assertThrows(FairNotFoundException.class,
                () -> queryFairDetailUseCase.execute(-1L));

        verify(fairFacade, times(1)).getFairDetail(anyLong());
        verify(attendeeRepository, never()).countByFair(any(Fair.class));
    }
}