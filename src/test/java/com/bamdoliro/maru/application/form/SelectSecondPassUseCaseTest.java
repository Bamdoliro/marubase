package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@Slf4j
@ActiveProfiles("test")
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

    @BeforeEach
    void setUp() {
        List<User> userList = userRepository.saveAll(
                UserFixture.generateUserList(FixedNumber.TOTAL * 2)
        );
        List<Form> formList = FormFixture.generateFormList(userList);
        formList.forEach(form -> {
            assignExaminationNumberService.execute(form);
            form.receive();
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
        assertEquals(FixedNumber.TOTAL, passedFormCount);
    }

    @Test
    void 마이스터전형_또는_사회통합전형에서_불합격을_한다면_일반전형으로_다시_지원한다() {
        selectSecondPassUseCase.execute();

        List<Form> failedFormList = formRepository.findByType(FormType.REGULAR)
                .stream()
                .filter(form -> form.isFailedNow() || form.isFirstFailedNow())
                .toList();

        assertEquals((long) formRepository.findAll().size() -  FixedNumber.TOTAL, failedFormList.size());
    }
}
