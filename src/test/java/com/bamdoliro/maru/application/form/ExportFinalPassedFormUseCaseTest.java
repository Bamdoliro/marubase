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
import com.bamdoliro.maru.shared.util.SaveFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class ExportFinalPassedFormUseCaseTest {

    @Autowired
    private ExportFinalPassedFormUseCase exportFinalPassedFormUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignExaminationNumberService assignExaminationNumberService;

    @Autowired
    private FormRepository formRepository;;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @BeforeEach
    void setUp() {
        List<User> userList = userRepository.saveAll(
                UserFixture.generateUserList(FixedNumber.TOTAL)
        );
        List<Form> formList = FormFixture.generateFormList(userList);
        formList.forEach(form -> {
            assignExaminationNumberService.execute(form);
            form.pass();
            calculateFormScoreService.execute(form);
            form.getScore().updateSecondRoundMeisterScore(100.2, 10.4, 100.7);
            formRepository.save(form);
        });
    }

    @Test
    void 최종합격자_명단을_엑셀로_저장한다() throws Exception {
        SaveFileUtil.execute(exportFinalPassedFormUseCase.execute(), SaveFileUtil.XLSX);
    }
}