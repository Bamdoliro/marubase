package com.bamdoliro.maru.infrastructure.aop.schedule;

import com.bamdoliro.maru.domain.form.exception.OutOfApplicationFormPeriodException;
import com.bamdoliro.maru.shared.constants.Schedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ScheduleValidationAspectTest {

    @InjectMocks
    private ScheduleValidationAspect scheduleValidationAspect;

    @Test
    void 날짜_및_시간_검증에_성공한다() {
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(Schedule.START.plusSeconds(1));

            scheduleValidationAspect.validateApplicationFormPeriod();
        }
    }

    @Test
    void 날짜_및_시간_검증에_실패하면_에러가_발생한다() {
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(Schedule.START.minusSeconds(1));

            assertThrows(OutOfApplicationFormPeriodException.class, () -> scheduleValidationAspect.validateApplicationFormPeriod());
        }
    }
}
