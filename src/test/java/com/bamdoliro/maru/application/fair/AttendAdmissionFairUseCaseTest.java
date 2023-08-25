package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.domain.fair.exception.HeadcountExceededException;
import com.bamdoliro.maru.domain.fair.exception.NotApplicationPeriodException;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttendAdmissionFairUseCaseTest {

    @InjectMocks
    private AttendAdmissionFairUseCase attendAdmissionFairUseCase;

    @Mock
    private FairFacade fairFacade;

    @Mock
    private AttendeeRepository attendeeRepository;

    @Mock
    private SendMessageService sendMessageService;

    @Test
    void 입학설명회에_참가_신청을_한다() {
        // given
        Fair fair = FairFixture.createFair();
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();
        Attendee attendee = FairFixture.createAttendee(fair);

        given(fairFacade.getFair(fair.getId())).willReturn(fair);
        given(attendeeRepository.countByFair(fair)).willReturn(20);
        given(attendeeRepository.save(any(Attendee.class))).willReturn(attendee);
        ArgumentCaptor<Attendee> captor = ArgumentCaptor.forClass(Attendee.class);

        // when
        attendAdmissionFairUseCase.execute(fair.getId(), request);

        // then
        verify(fairFacade, times(1)).getFair(fair.getId());
        verify(attendeeRepository, times(1)).countByFair(fair);
        verify(attendeeRepository, times(1)).save(captor.capture());
        verify(sendMessageService, times(1)).execute(anyString(), anyString(), anyString());

        Attendee savedAttendee = captor.getValue();
        assertEquals(attendee.getName(), savedAttendee.getName());
        assertEquals(attendee.getQuestion(), savedAttendee.getQuestion());
    }

    @Test
    void 입학설명회에_참가_신청을_할_때_해당_설명회가_없다면_에러가_발생한다() {
        // given
        Fair fair = FairFixture.createFair();
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();

        willThrow(new FairNotFoundException()).given(fairFacade).getFair(fair.getId());

        // when and then
        assertThrows(FairNotFoundException.class,
                () -> attendAdmissionFairUseCase.execute(fair.getId(), request));

        verify(fairFacade, times(1)).getFair(fair.getId());
        verify(attendeeRepository, never()).countByFair(any());
        verify(attendeeRepository, never()).save(any());
        verify(sendMessageService, never()).execute(anyString(), anyString(), anyString());
    }

    @Test
    void 입학설명회에_참가_신청을_할_때_인원_수를_초과했으면_에러가_발생한다() {
        // given
        Fair fair = FairFixture.createFair();
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();

        given(fairFacade.getFair(fair.getId())).willReturn(fair);
        given(attendeeRepository.countByFair(fair)).willReturn(121);

        // when and then
        assertThrows(HeadcountExceededException.class,
                () -> attendAdmissionFairUseCase.execute(fair.getId(), request));

        verify(fairFacade, times(1)).getFair(fair.getId());
        verify(attendeeRepository, times(1)).countByFair(fair);
        verify(attendeeRepository, never()).save(any());
        verify(sendMessageService, never()).execute(anyString(), anyString(), anyString());
    }

    @Test
    void 입학설명회에_참가_신청을_할_때_신청_기간이_아니라면_에러가_발생한다() {
        Fair fair = FairFixture.createClosedFair();
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();

        given(fairFacade.getFair(fair.getId())).willReturn(fair);

        // when and then
        assertThrows(NotApplicationPeriodException.class,
                () -> attendAdmissionFairUseCase.execute(fair.getId(), request));

        verify(fairFacade, times(1)).getFair(fair.getId());
        verify(attendeeRepository, never()).countByFair(fair);
        verify(attendeeRepository, never()).save(any());
        verify(sendMessageService, never()).execute(anyString(), anyString());
    }
}