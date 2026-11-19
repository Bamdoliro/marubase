package com.bamdoliro.maru.application.schedule;

import com.bamdoliro.maru.application.form.PassOrFailFormUseCase;
import com.bamdoliro.maru.application.form.SelectFirstPassUseCase;
import com.bamdoliro.maru.application.form.SelectSecondPassUseCase;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.exception.MissingTotalScoreException;
import com.bamdoliro.maru.domain.form.exception.NoSelectionCandidatesException;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.schedule.exception.FirstPassAlreadyExecutedException;
import com.bamdoliro.maru.domain.schedule.exception.ScheduleNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.FirstPassSelectionExecutionRepository;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormRequest;
import com.bamdoliro.maru.shared.config.JasyptConfig;
import com.bamdoliro.maru.shared.config.JpaConfig;
import com.bamdoliro.maru.shared.config.QuerydslConfig;
import com.bamdoliro.maru.shared.config.properties.JasyptProperties;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.error.MaruException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({QuerydslConfig.class, JasyptConfig.class, JpaConfig.class,
        AdmissionScheduleFacade.class, ExecuteFirstPassSelectionUseCase.class,
        ExecuteSecondPassSelectionUseCase.class, SelectFirstPassUseCase.class,
        SelectSecondPassUseCase.class, PassOrFailFormUseCase.class, CalculateFormScoreService.class})
@EnableConfigurationProperties(JasyptProperties.class)
class ManualSelectionIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private AdmissionScheduleRepository schedules;
    @Autowired private FirstPassSelectionExecutionRepository executions;
    @Autowired private FormRepository forms;
    @Autowired private ExecuteFirstPassSelectionUseCase first;
    @Autowired private ExecuteSecondPassSelectionUseCase second;
    @Autowired private PassOrFailFormUseCase results;
    @MockitoSpyBean private SelectFirstPassUseCase firstAlgorithm;

    @BeforeEach
    void setUp() {
        tx().executeWithoutResult(status -> {
            executions.deleteAll();
            forms.deleteAll();
            entityManager.flush();
            entityManager.createQuery("delete from User").executeUpdate();
            schedules.deleteAll();
            schedules.flush();
            schedules.saveAndFlush(ScheduleFixture.createSchedule());
        });
    }

    @Test
    void 승인_원서만_일괄_합격불합격을_확정한다() {
        int quota = (int) Math.ceil(FixedNumber.TOTAL * FixedNumber.MULTIPLE);
        List<Long> ids = tx().execute(status -> {
            List<Long> saved = new ArrayList<>();
            for (int i = 0; i <= quota; i++) {
                Form form = newForm(FormStatus.APPROVED);
                ReflectionTestUtils.setField(form.getEducation().getSchool(), "location", "부산광역시");
                form.updateScore(new Score(200.0 - i, 0, 0, 0));
                saved.add(save(form));
            }
            return saved;
        });
        Long submitted = save(FormStatus.SUBMITTED);
        Long rejected = save(FormStatus.REJECTED);
        Long finalSubmitted = save(FormStatus.FINAL_SUBMITTED);
        Long received = save(FormStatus.RECEIVED);

        first.execute();

        assertThat(forms.findByStatus(FormStatus.FIRST_PASSED)).hasSize(quota);
        assertThat(statusOf(ids.getLast())).isEqualTo(FormStatus.FIRST_FAILED);
        assertThat(statusOf(submitted)).isEqualTo(FormStatus.SUBMITTED);
        assertThat(statusOf(rejected)).isEqualTo(FormStatus.REJECTED);
        assertThat(statusOf(finalSubmitted)).isEqualTo(FormStatus.FINAL_SUBMITTED);
        assertThat(statusOf(received)).isEqualTo(FormStatus.RECEIVED);
        assertThat(executions.count()).isEqualTo(1);
        assertThatThrownBy(first::execute).isInstanceOf(FirstPassAlreadyExecutedException.class);
    }

    @Test
    void 선발_실패시_일부_결과와_실행기록을_함께_롤백한다() {
        Long id = save(FormStatus.APPROVED);
        doAnswer(invocation -> {
            invocation.callRealMethod();
            throw new IllegalStateException("synthetic selection failure");
        }).when(firstAlgorithm).execute();

        assertThatThrownBy(first::execute).isInstanceOf(IllegalStateException.class);
        assertThat(statusOf(id)).isEqualTo(FormStatus.APPROVED);
        assertThat(executions.count()).isZero();
    }

    @Test
    void 대상이_없으면_일차_실행기록을_소비하지_않는다() {
        save(FormStatus.REJECTED);
        assertThatThrownBy(first::execute).isInstanceOf(NoSelectionCandidatesException.class);
        assertThat(executions.count()).isZero();
        save(FormStatus.APPROVED);
        first.execute();
        assertThat(executions.count()).isEqualTo(1);
    }

    @Test
    void 이차_선발은_일차합격자만_처리하고_연속호출을_거부한다() {
        Long passed = save(FormStatus.FIRST_PASSED);
        Long approved = save(FormStatus.APPROVED);
        Long rejected = save(FormStatus.REJECTED);
        tx().executeWithoutResult(status -> forms.findById(passed).orElseThrow()
                .getScore().updateSecondRoundScore(100.0, 50.0));

        second.execute();

        assertThat(statusOf(passed)).isEqualTo(FormStatus.PASSED);
        assertThat(statusOf(approved)).isEqualTo(FormStatus.APPROVED);
        assertThat(statusOf(rejected)).isEqualTo(FormStatus.REJECTED);
        assertThatThrownBy(second::execute).isInstanceOf(NoSelectionCandidatesException.class);
    }

    @Test
    void 최종점수가_누락되면_이차_전체를_유지한다() {
        Long ready = save(FormStatus.FIRST_PASSED);
        Long missing = save(FormStatus.FIRST_PASSED);
        tx().executeWithoutResult(status -> forms.findById(ready).orElseThrow()
                .getScore().updateSecondRoundScore(100.0, 50.0));
        assertThatThrownBy(second::execute).isInstanceOf(MissingTotalScoreException.class);
        assertThat(statusOf(ready)).isEqualTo(FormStatus.FIRST_PASSED);
        assertThat(statusOf(missing)).isEqualTo(FormStatus.FIRST_PASSED);
    }

    @Test
    void 미승인_원서가_섞인_수동변경은_아무것도_저장하지_않는다() {
        Long ready = save(FormStatus.FIRST_PASSED);
        Long rejected = save(FormStatus.REJECTED);
        assertThatThrownBy(() -> results.execute(new PassOrFailFormListRequest(List.of(
                new PassOrFailFormRequest(ready, true), new PassOrFailFormRequest(rejected, true)))))
                .isInstanceOf(InvalidFormStatusException.class);
        assertThat(statusOf(ready)).isEqualTo(FormStatus.FIRST_PASSED);
        assertThat(statusOf(rejected)).isEqualTo(FormStatus.REJECTED);
    }

    @Test
    void 수동변경은_순서와_무관하게_ID별_결과를_저장한다() {
        Long firstId = save(FormStatus.FIRST_PASSED);
        Long secondId = save(FormStatus.FIRST_PASSED);
        results.execute(new PassOrFailFormListRequest(List.of(
                new PassOrFailFormRequest(secondId, false), new PassOrFailFormRequest(firstId, true))));
        assertThat(statusOf(firstId)).isEqualTo(FormStatus.PASSED);
        assertThat(statusOf(secondId)).isEqualTo(FormStatus.FAILED);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void 두_동시요청은_한번만_선발한다(boolean firstRound) throws Exception {
        Long id = save(firstRound ? FormStatus.APPROVED : FormStatus.FIRST_PASSED);
        if (!firstRound) {
            tx().executeWithoutResult(status -> forms.findById(id).orElseThrow()
                    .getScore().updateSecondRoundScore(100.0, 50.0));
        }
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<String> call = () -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("start timeout");
                }
                try {
                    if (firstRound) {
                        first.execute();
                    } else {
                        second.execute();
                    }
                    return "success";
                } catch (MaruException e) {
                    if ((firstRound && e instanceof FirstPassAlreadyExecutedException)
                            || (!firstRound && e instanceof NoSelectionCandidatesException)) {
                        return "duplicate";
                    }
                    throw e;
                }
            };
            var left = executor.submit(call);
            var right = executor.submit(call);
            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(List.of(left.get(20, TimeUnit.SECONDS), right.get(20, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder("success", "duplicate");
        } finally {
            start.countDown();
        }
        assertThat(executions.count()).isEqualTo(firstRound ? 1 : 0);
        assertThat(statusOf(id)).isEqualTo(firstRound ? FormStatus.FIRST_PASSED : FormStatus.PASSED);
    }

    @Test
    void 현재_일정_없이는_선발하지_않는다() {
        schedules.deleteAll();
        assertThatThrownBy(first::execute).isInstanceOf(ScheduleNotFoundException.class);
        assertThatThrownBy(second::execute).isInstanceOf(ScheduleNotFoundException.class);
        assertThat(executions.count()).isZero();
    }

    private Long save(FormStatus status) {
        return tx().execute(transaction -> save(newForm(status)));
    }

    private Form newForm(FormStatus status) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "status", status);
        return form;
    }

    private Long save(Form form) {
        entityManager.persist(form.getUser());
        entityManager.persist(form);
        form.assignExaminationNumber(1000L + form.getId());
        return form.getId();
    }

    private FormStatus statusOf(Long id) {
        return forms.findById(id).orElseThrow().getStatus();
    }

    private TransactionTemplate tx() {
        return new TransactionTemplate(transactionManager);
    }
}
