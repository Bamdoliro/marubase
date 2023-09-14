package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
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
    }

    @Test
    void 정상적으로_1차전형_합격자를_선발한다() {
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
        int passedFormCount = formList.stream().filter(Form::isFirstPassedNow).toList().size();
        assertEquals(Math.round(FixedNumber.TOTAL * FixedNumber.MULTIPLE), passedFormCount);
    }
}