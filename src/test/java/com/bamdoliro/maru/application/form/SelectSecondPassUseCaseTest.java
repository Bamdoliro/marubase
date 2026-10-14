package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
public class SelectSecondPassUseCaseTest {

    @Autowired
    private SelectSecondPassUseCase selectSecondPassUseCase;

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

    @BeforeEach
    void setUp() {
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
        selectFirstPassUseCase.execute();
        List<Form> firstPassedFormList = formRepository.findByStatus(FormStatus.FIRST_PASSED);
        firstPassedFormList.forEach(form -> {
            if (form.getType().isMeister()) {
                form.getScore().updateSecondRoundMeisterScore(RandomUtil.randomDouble(0, 100), RandomUtil.randomDouble(0, 100), RandomUtil.randomDouble(0, 100));
            } else {
                form.getScore().updateSecondRoundScore(RandomUtil.randomDouble(0, 100), RandomUtil.randomDouble(0, 100));
            }
            formRepository.save(form);
        });
    }

    @Test
    void 정상적으로_2차전형_합격자를_선발한다() {
        setUpRandomForms();
        selectSecondPassUseCase.execute();

        Comparator<Form> comparator = Comparator
                .comparing(Form::getType)
                .thenComparing(form -> form.getScore().getTotalScore());

        List<Form> formList = formRepository.findAll()
                .stream()
                .filter(form -> form.isPassedNow() || form.isFailedNow())
                .sorted(comparator)
                .toList();

        formList.forEach(form -> {
            log.info("====================");
            log.info("id: {}", form.getId());
            log.info("examinationNumber: {}", form.getExaminationNumber());
            log.info("type: {}", form.getType());
            log.info("score: {}", form.getScore().getTotalScore());
            log.info("status: {}", form.getStatus());
        });
        int passedFormCount = (int) formList.stream().filter(Form::isPassedNow).count();
        int passedOtherRegionFormCount = (int) formList.stream().filter(form -> form.isPassedNow() && !form.getEducation().getSchool().isBusan()).count();
        assertEquals(FixedNumber.TOTAL, passedFormCount);
        assertTrue(FixedNumber.TOTAL / 2 >= passedOtherRegionFormCount);
    }

    @Test
    void 마이스터전형_또는_사회통합전형에서_불합격을_한다면_일반전형으로_다시_지원한다() {
        setUpRandomForms();
        selectSecondPassUseCase.execute();

        List<Form> failedFormList = formRepository.findByType(FormType.REGULAR)
                .stream()
                .filter(form -> form.isFailedNow() || form.isFirstFailedNow())
                .toList();

        assertEquals((long) formRepository.findAll().size() -  FixedNumber.TOTAL, failedFormList.size());
    }

    @Test
    void 기회균등이_미달이면_남는_자리를_사회다양성으로_넘긴다() {
        // given
        scenario.saveApprovedForms(13, 1, 6);
        selectFirstPassUseCase.execute();
        scenario.updateSecondRoundScoreToFirstRoundScore();

        // when
        selectSecondPassUseCase.execute();

        // then
        // 1차 합격 기회균등 1 / 사회다양성 6 → 사회통합 6석: 3 / 3 → 기회균등 미달 2석을 사회다양성으로
        assertEquals("regular=48, meisterTalent=10, equalOpportunity=1, societyDiversity=5", scenario.countPassedForms(Form::isPassedNow));
    }

    @Test
    void 기회균등_지원자가_없으면_사회다양성이_사회통합_정원을_모두_가져간다() {
        // given
        scenario.saveApprovedForms(13, 0, 20);
        selectFirstPassUseCase.execute();
        scenario.updateSecondRoundScoreToFirstRoundScore();

        // when
        selectSecondPassUseCase.execute();

        // then
        // 1차 합격 사회다양성 8 → 사회통합 6석 전부 사회다양성
        assertEquals("regular=48, meisterTalent=10, equalOpportunity=0, societyDiversity=6", scenario.countPassedForms(Form::isPassedNow));
    }

    @Test
    void 사회다양성이_미달이면_남는_자리를_기회균등으로_넘긴다() {
        // given
        scenario.saveApprovedForms(13, 20, 1);
        selectFirstPassUseCase.execute();
        scenario.updateSecondRoundScoreToFirstRoundScore();

        // when
        selectSecondPassUseCase.execute();

        // then
        // 1차 합격 기회균등 7 / 사회다양성 1 → 사회다양성 미달 2석을 기회균등으로
        assertEquals("regular=48, meisterTalent=10, equalOpportunity=5, societyDiversity=1", scenario.countPassedForms(Form::isPassedNow));
    }

    @Test
    void 사회통합_전체가_미달이면_남는_자리만큼_일반전형_정원을_늘린다() {
        // given
        scenario.saveApprovedForms(13, 1, 2);
        selectFirstPassUseCase.execute();
        scenario.updateSecondRoundScoreToFirstRoundScore();

        // when
        selectSecondPassUseCase.execute();

        // then
        // 1차 합격 사회통합 3명 < 원 정원 6 → 일반 48 + 3 = 51
        assertEquals("regular=51, meisterTalent=10, equalOpportunity=1, societyDiversity=2", scenario.countPassedForms(Form::isPassedNow));
    }

    @Test
    void 사회통합_양쪽_모두_정원_이상이면_반반_선발한다() {
        // given
        scenario.saveApprovedForms(13, 10, 10);
        selectFirstPassUseCase.execute();
        scenario.updateSecondRoundScoreToFirstRoundScore();

        // when
        selectSecondPassUseCase.execute();

        // then
        // 사회통합 6석 → 3 / 3
        assertEquals("regular=48, meisterTalent=10, equalOpportunity=3, societyDiversity=3", scenario.countPassedForms(Form::isPassedNow));
    }

    @Test
    void 마이스터인재전형이_미달이면_일반전형_정원을_늘린다() {
        // given
        scenario.saveApprovedForms(5, 10, 10);
        selectFirstPassUseCase.execute();
        scenario.updateSecondRoundScoreToFirstRoundScore();

        // when
        selectSecondPassUseCase.execute();

        // then
        // 1차 합격 마이스터 5명 < 원 정원 10 → 일반 48 + 5 = 53
        assertEquals("regular=53, meisterTalent=5, equalOpportunity=3, societyDiversity=3", scenario.countPassedForms(Form::isPassedNow));
    }
}
