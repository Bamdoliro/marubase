package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.SaveFileUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class GenerateAdmissionTicketUseCaseSpringBootTest {

    @Autowired
    private GenerateAdmissionTicketUseCase generateAdmissionTicketUseCase;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Transactional
    @Test
    void 수험표를_저장한다() throws IOException {
        User user = userRepository.save(UserFixture.createUser());
        Form form = FormFixture.createRandomForm(user);
        form.assignExaminationNumber(2004L);
        form.firstPass();
        calculateFormScoreService.execute(form);
        formRepository.save(form);

        SaveFileUtil.execute(generateAdmissionTicketUseCase.execute(user), SaveFileUtil.PDF);
    }

}