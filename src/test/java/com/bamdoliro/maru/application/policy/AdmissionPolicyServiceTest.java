package com.bamdoliro.maru.application.policy;

import com.bamdoliro.maru.domain.policy.PolicyException;
import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import com.bamdoliro.maru.domain.policy.TicketNotice;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.infrastructure.persistence.policy.AdmissionPolicyRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.presentation.policy.dto.PolicyRequest;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class AdmissionPolicyServiceTest {
    @Autowired
    private AdmissionPolicyService service;
    @Autowired
    private AdmissionScheduleRepository scheduleRepository;
    @Autowired
    private AdmissionPolicyRepository policyRepository;
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private UserRepository userRepository;

    private AdmissionSchedule saveSchedule(boolean future) {
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        if (future) {
            // 정책 잠금만 테스트한다. 입학 일정 자체의 기간 검증은 일정 테스트에서 다룬다.
            ReflectionTestUtils.setField(schedule, "applicationFormStart",
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusYears(1));
        }
        return scheduleRepository.saveAndFlush(schedule);
    }

    private SelectionPolicy changedSelection() {
        return new SelectionPolicy(50, 12, 8, 1, 1, new BigDecimal("1.5"), new BigDecimal("0.4"));
    }

    @Test
    void 미등록_정책은_기존값을_읽고_DB를_수정하지_않는다() {
        saveSchedule(true);
        var response = service.current();
        assertThat(response.selection()).isEqualTo(SelectionPolicy.defaults());
        assertThat(response.ticket()).isEqualTo(TicketNotice.defaults());
        assertThat(response.version()).isEqualTo(-1);
        assertThat(response.selectionLocked()).isFalse();
        assertThat(policyRepository.count()).isZero();
    }

    @Test
    void 정책을_수정하고_전후값과_수정자를_기록한다() {
        AdmissionSchedule schedule = saveSchedule(true);
        var updated = service.update(7L, new PolicyRequest(
                schedule.getId(), -1L, changedSelection(), TicketNotice.defaults()));
        assertThat(updated.version()).isZero();
        assertThat(updated.selection().total()).isEqualTo(70);
        assertThat(service.forYear(schedule.getAdmissionYear()).selection()).isEqualTo(changedSelection());
        assertThat(service.history(schedule.getAdmissionYear())).singleElement().satisfies(history -> {
            assertThat(history.userId()).isEqualTo(7);
            assertThat(history.beforePolicy()).contains("\"regular\":48");
            assertThat(history.afterPolicy()).contains("\"regular\":50");
        });
    }

    @Test
    void 오래된_버전과_다른_일정_수정은_거부한다() {
        AdmissionSchedule schedule = saveSchedule(true);
        PolicyRequest request = new PolicyRequest(schedule.getId(), -1L, changedSelection(), TicketNotice.defaults());
        service.update(7L, request);
        assertThatThrownBy(() -> service.update(7L, request)).isInstanceOf(PolicyException.class);
        assertThatThrownBy(() -> service.update(7L, new PolicyRequest(
                schedule.getId() + 1, 0L, changedSelection(), TicketNotice.defaults())))
                .isInstanceOf(PolicyException.class);
        assertThat(service.history(schedule.getAdmissionYear())).hasSize(1);
    }

    @Test
    void 접수시작_후에는_정원은_잠그고_안내문만_수정한다() {
        AdmissionSchedule schedule = saveSchedule(false);
        assertThat(service.current().selectionLocked()).isTrue();
        assertThatThrownBy(() -> service.update(7L, new PolicyRequest(
                schedule.getId(), -1L, changedSelection(), TicketNotice.defaults())))
                .isInstanceOf(PolicyException.class);
        TicketNotice notice = new TicketNotice("신관 평가장", "학교 홈페이지", "행정실",
                "별도 안내", "직접 제출", "도시락 지참", "수험표", "필기구", "전자기기");
        var updated = service.update(7L, new PolicyRequest(
                schedule.getId(), -1L, SelectionPolicy.defaults(), notice));
        assertThat(updated.ticket()).isEqualTo(notice);
        assertThat(service.ticketFor(schedule.getId())).isEqualTo(notice);
        assertThat(policyRepository.findById(schedule.getId()).orElseThrow().isSelectionLocked()).isTrue();
    }

    @Test
    void 선발에_사용한_정원은_영구_잠금한다() {
        AdmissionSchedule schedule = saveSchedule(true);
        service.update(7L, new PolicyRequest(schedule.getId(), -1L, changedSelection(), TicketNotice.defaults()));
        assertThat(service.selectionForExecution()).isEqualTo(changedSelection());
        var locked = service.current();
        assertThat(locked.selectionLocked()).isTrue();
        assertThatThrownBy(() -> service.update(7L, new PolicyRequest(
                schedule.getId(), locked.version(), SelectionPolicy.defaults(), TicketNotice.defaults())))
                .isInstanceOf(PolicyException.class);
    }

    @Test
    void 합계가_0인_정원은_저장하지_않는다() {
        AdmissionSchedule schedule = saveSchedule(true);
        var empty = new SelectionPolicy(0, 0, 0, 0, 0, BigDecimal.ONE, BigDecimal.ZERO);
        assertThatThrownBy(() -> service.update(7L, new PolicyRequest(
                schedule.getId(), -1L, empty, TicketNotice.defaults()))).isInstanceOf(PolicyException.class);
        assertThat(policyRepository.count()).isZero();
    }

    @Test
    void 접수_전이어도_원서가_존재하면_정원_수정을_막는다() {
        AdmissionSchedule schedule = saveSchedule(true);
        var user = userRepository.save(UserFixture.createUser());
        var form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "user", user);
        formRepository.saveAndFlush(form);
        assertThat(service.current().selectionLocked()).isTrue();
        assertThatThrownBy(() -> service.update(7L, new PolicyRequest(
                schedule.getId(), -1L, changedSelection(), TicketNotice.defaults())))
                .isInstanceOf(PolicyException.class);
    }

    @Test
    void 동시_최초수정은_하나만_성공한다() throws Exception {
        AdmissionSchedule schedule = saveSchedule(true);
        PolicyRequest request = new PolicyRequest(schedule.getId(), -1L, changedSelection(), TicketNotice.defaults());
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Boolean> update = () -> {
                if (!start.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("동시 요청 시작 실패");
                }
                try {
                    service.update(7L, request);
                    return true;
                } catch (PolicyException exception) {
                    assertThat(exception.getErrorProperty()).isEqualTo(PolicyException.Reason.POLICY_VERSION_CONFLICT);
                    return false;
                }
            };
            var first = executor.submit(update);
            var second = executor.submit(update);
            start.countDown();
            assertThat(first.get(15, TimeUnit.SECONDS)).isNotEqualTo(second.get(15, TimeUnit.SECONDS));
        }
        assertThat(policyRepository.count()).isEqualTo(1);
        assertThat(service.history(schedule.getAdmissionYear())).hasSize(1);
    }
}
