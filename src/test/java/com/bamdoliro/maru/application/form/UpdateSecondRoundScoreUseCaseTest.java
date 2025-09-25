package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class UpdateSecondRoundScoreUseCaseTest {

    @Autowired
    private UpdateSecondRoundScoreUseCase updateSecondRoundScoreUseCase;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private SelectFirstPassUseCase selectFirstPassUseCase;

    @Autowired
    private AssignExaminationNumberService assignExaminationNumberService;

    @BeforeEach
    void setUp() {
        List<User> userList = userRepository.saveAll(
                UserFixture.generateUserList(FixedNumber.TOTAL * 2)
        );
        List<Form> formList = FormFixture.generateBusanFormList(userList);
        formList.forEach(form -> {
            assignExaminationNumberService.execute(form);
            form.approve();
            calculateFormScoreService.execute(form);
            if (form.getExaminationNumber() == 1001 ||
                    form.getExaminationNumber() == 1002 ||
                    form.getExaminationNumber() == 2001 ||
                    form.getExaminationNumber() == 3001
            ) {
                formRepository.save(form);
            }
        });
        selectFirstPassUseCase.execute();
    }

    @Test
    void 정상적으로_2차전형_점수를_입력한다() throws IOException {
        // given
        File file = new ClassPathResource("xlsx/2차전형점수.xlsx").getFile();
        MockMultipartFile multipartFile = new MockMultipartFile("test.xlsx", new FileInputStream(file));

        // when
        updateSecondRoundScoreUseCase.execute(multipartFile);

        // then
        List<Form> formList = formRepository.findByStatus(FormStatus.FIRST_PASSED).stream()
                .sorted(Comparator.comparing(Form::getExaminationNumber))
                .toList();
        assertEquals(3, formList.size());
        assertNull(formList.get(0).getScore().getCodingTestScore());
        assertEquals(20, formList.get(1).getScore().getCodingTestScore());
        assertNull(formList.get(2).getScore().getCodingTestScore());
    }
}