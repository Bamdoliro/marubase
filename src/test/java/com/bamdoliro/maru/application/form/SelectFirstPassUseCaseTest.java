package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.application.policy.AdmissionPolicyService;
import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import com.bamdoliro.maru.domain.policy.TicketNotice;
import com.bamdoliro.maru.presentation.policy.dto.PolicyRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class SelectFirstPassUseCaseTest {

    @Autowired
    private SelectFirstPassUseCase selectFirstPassUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private AssignExaminationNumberService assignExaminationNumberService;

    private SelectionScenarioSupport scenario;

    @Autowired
    private AdmissionScheduleRepository scheduleRepository;

    @Autowired
    private AdmissionPolicyService policyService;

    @BeforeEach
    void setUp() {
        scheduleRepository.save(ScheduleFixture.createSchedule());
        scenario = new SelectionScenarioSupport(userRepository, formRepository, assignExaminationNumberService, calculateFormScoreService);
    }

    private void setUpRandomForms() {
        List<User> userList = userRepository.saveAll(
                UserFixture.generateUserList(FixedNumber.TOTAL * 2)
        );
        List<Form> formList = FormFixture.generateBusanFormList(userList.subList(0, userList.size() / 2));
        formList.addAll(FormFixture.generateOtherRegionFormList(userList.subList(userList.size() / 2, userList.size())));
        formList.forEach(form -> {
            assignExaminationNumberService.execute(form);
            form.approve();
            calculateFormScoreService.execute(form);
            formRepository.save(form);
        });
    }

    @Test
    void 관리자_설정_정원이_실제_선발에_적용된다() {
        var schedule = scheduleRepository.findByActiveTrue().orElseThrow();
        ReflectionTestUtils.setField(schedule, "applicationFormStart", LocalDateTime.now().plusYears(1));
        scheduleRepository.saveAndFlush(schedule);
        var policy = new SelectionPolicy(40, 8, 4, 1, 1, new BigDecimal("1.3"), new BigDecimal("0.5"));
        policyService.update(7L, new PolicyRequest(schedule.getId(), -1L, policy, TicketNotice.defaults()));
        scenario.saveApprovedForms(13, 10, 10);

        selectFirstPassUseCase.execute();

        assertEquals("regular=52, meisterTalent=11, equalOpportunity=3, societyDiversity=3",
                scenario.countPassedForms(Form::isFirstPassedNow));
        assertTrue(policyService.current().selectionLocked());
    }

    @Test
    void 정상적으로_1차전형_합격자를_선발한다() {
        setUpRandomForms();
        selectFirstPassUseCase.execute();

        Comparator<Form> compare = Comparator
                .comparing(Form::getType)
                .thenComparing(f -> f.getScore().getFirstRoundScore());

        List<Form> formList = formRepository.findAll()
                .stream()
                .sorted(compare)
                .toList();

        formList.forEach(form -> {
            log.info("====================");
            log.info("id: {}", form.getId());
            log.info("examinationNumber: {}", form.getExaminationNumber());
            log.info("type: {}", form.getType());
            log.info("score: {}", form.getScore().getFirstRoundScore());
            log.info("status: {}", form.getStatus());
        });
        int passedFormCount = (int) formList.stream().filter(Form::isFirstPassedNow).count();
        int passedOtherRegionFormCount = (int) formList.stream().filter(form -> form.isFirstPassedNow() && !form.getEducation().getSchool().isBusan()).count();
        int totalCount = (int) Math.ceil(FixedNumber.TOTAL * FixedNumber.MULTIPLE);
        assertEquals(totalCount, passedFormCount);
        assertTrue(Math.ceil((double) totalCount / 2) >= passedOtherRegionFormCount);
    }

    @Test
    void 기회균등이_미달이면_남는_자리를_사회다양성으로_넘긴다() {
        // given
        scenario.saveApprovedForms(13, 1, 6);

        // when
        selectFirstPassUseCase.execute();

        // then
        // 사회통합 8석: 기회균등 미달 3석을 사회다양성으로 넘기지만 사회다양성 지원자가 6명이라 6명 선발
        assertEquals("regular=63, meisterTalent=13, equalOpportunity=1, societyDiversity=6", scenario.countPassedForms(Form::isFirstPassedNow));
    }

    @Test
    void 기회균등_지원자가_없으면_사회다양성이_사회통합_정원을_모두_가져간다() {
        // given
        scenario.saveApprovedForms(13, 0, 20);

        // when
        selectFirstPassUseCase.execute();

        // then
        // 사회통합 8석 전부 사회다양성
        assertEquals("regular=63, meisterTalent=13, equalOpportunity=0, societyDiversity=8", scenario.countPassedForms(Form::isFirstPassedNow));
    }

    @Test
    void 사회다양성이_미달이면_남는_자리를_기회균등으로_넘긴다() {
        // given
        scenario.saveApprovedForms(13, 20, 1);

        // when
        selectFirstPassUseCase.execute();

        // then
        // 사회통합 8석: 사회다양성 미달 3석을 기회균등으로
        assertEquals("regular=63, meisterTalent=13, equalOpportunity=7, societyDiversity=1", scenario.countPassedForms(Form::isFirstPassedNow));
    }

    @Test
    void 사회통합_전체가_미달이면_남는_자리만큼_일반전형_정원을_늘린다() {
        // given
        scenario.saveApprovedForms(13, 1, 2);

        // when
        selectFirstPassUseCase.execute();

        // then
        // 사회통합 미달 3석 → 일반 ceil((48 + 3) * 1.3) = 67
        assertEquals("regular=67, meisterTalent=13, equalOpportunity=1, societyDiversity=2", scenario.countPassedForms(Form::isFirstPassedNow));
    }

    @Test
    void 사회통합_양쪽_모두_정원_이상이면_반반_선발한다() {
        // given
        scenario.saveApprovedForms(13, 10, 10);

        // when
        selectFirstPassUseCase.execute();

        // then
        // 사회통합 8석 → 4 / 4
        assertEquals("regular=63, meisterTalent=13, equalOpportunity=4, societyDiversity=4", scenario.countPassedForms(Form::isFirstPassedNow));
    }

    @Test
    void 마이스터인재전형이_미달이면_일반전형_정원을_늘린다() {
        // given
        scenario.saveApprovedForms(5, 10, 10);

        // when
        selectFirstPassUseCase.execute();

        // then
        // 마이스터 미달 5석 → 일반 ceil((48 + 5) * 1.3) = 69
        assertEquals("regular=69, meisterTalent=5, equalOpportunity=4, societyDiversity=4", scenario.countPassedForms(Form::isFirstPassedNow));
    }
}
