package com.bamdoliro.maru.infrastructure.persistence.schedule;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdmissionScheduleInitializerTest {

    @Mock
    private AdmissionScheduleRepository repository;

    @InjectMocks
    private AdmissionScheduleInitializer initializer;

    @Test
    void 빈_DB에_과거_일정을_자동으로_생성하지_않는다() {
        given(repository.findByActiveTrue()).willReturn(Optional.empty());
        given(repository.findFirstByOrderByAdmissionYearDesc()).willReturn(Optional.empty());

        initializer.run(null);

        verify(repository, never()).save(any());
    }

    @Test
    void 기존_활성_일정은_그대로_유지한다() {
        given(repository.findByActiveTrue()).willReturn(Optional.of(ScheduleFixture.createSchedule()));

        initializer.run(null);

        verify(repository, never()).findFirstByOrderByAdmissionYearDesc();
        verify(repository, never()).save(any());
    }

    @Test
    void 활성_일정이_없으면_저장된_최신_일정을_활성화한다() {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        schedule.deactivate();
        given(repository.findByActiveTrue()).willReturn(Optional.empty());
        given(repository.findFirstByOrderByAdmissionYearDesc()).willReturn(Optional.of(schedule));

        initializer.run(null);

        assertThat(schedule.isActive()).isTrue();
    }
}
