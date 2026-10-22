package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;

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
import com.bamdoliro.maru.shared.util.SaveFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class ExportFirstRoundResultUseCaseTest {

    @Autowired
    private ExportFirstRoundResultUseCase exportFirstRoundResultUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignExaminationNumberService assignExaminationNumberService;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private SelectFirstPassUseCase selectFirstPassUseCase;

    @Autowired
    private AdmissionScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        scheduleRepository.save(ScheduleFixture.createSchedule());
        List<User> userList = userRepository.saveAll(
                UserFixture.generateUserList(FixedNumber.TOTAL * 2)
        );
        List<Form> formList = FormFixture.generateBusanFormList(userList);
        formList.forEach(form -> {
            assignExaminationNumberService.execute(form);
            form.receive();
            calculateFormScoreService.execute(form);
            formRepository.save(form);
        });

        selectFirstPassUseCase.execute();
    }

    @Test
    void 정상적으로_1차전형_결과를_엑셀로_저장한다() throws IOException {
        SaveFileUtil.execute(exportFirstRoundResultUseCase.execute(), SaveFileUtil.XLSX);
    }
}
