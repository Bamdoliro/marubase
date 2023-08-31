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
import com.bamdoliro.maru.shared.util.SaveFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.bamdoliro.maru.domain.form.domain.type.FormStatus.SUBMITTED;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class ExportResultUseCaseTest {


    @Autowired
    private ExportResultUseCase exportResultUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignExaminationNumberService assignExaminationNumberService;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @BeforeEach
    void setUp() {
        List<User> userList = userRepository.saveAll(
                UserFixture.generateUserList(FixedNumber.TOTAL * 2)
        );
        List<Form> formList = FormFixture.generateFormList(userList);
        formList.forEach(form -> {
            assignExaminationNumberService.execute(form);

            switch (randomFormStatus()) {
                case FINAL_SUBMITTED -> form.submit("");
                case APPROVED -> form.approve();
                case REJECTED -> form.reject();
                case RECEIVED -> form.receive();
                case FIRST_PASSED -> form.firstPass();
                case FIRST_FAILED -> form.firstFail();
                case PASSED-> form.pass();
                case FAILED -> form.fail();
                case NO_SHOW -> form.noShow();
            }

            calculateFormScoreService.execute(form);
            form.getScore().updateSecondRoundMeisterScore(100.2, 10.4, 100.7);
            formRepository.save(form);
        });
    }

    private static FormStatus randomFormStatus() {
        FormStatus[] values = FormStatus.values();
        return values[new Random().nextInt(values.length)];
    }

    @Test
    void 정상적으로_전체_결과를_다운받는다() throws IOException {
        SaveFileUtil.execute(exportResultUseCase.execute(), SaveFileUtil.XLSX);
    }
}