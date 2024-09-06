package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
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
public class GenerateProofOfApplicationUseCaseSpringBootTest {

    @Autowired
    private GenerateProofOfApplicationUseCase generateProofOfApplicationUseCase;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    void 접수증을_저장한다() throws IOException {
        User user = userRepository.save(UserFixture.createUser());
        Form form = FormFixture.createRandomForm(user);
        form.submit();
        formRepository.save(form);

        SaveFileUtil.execute(generateProofOfApplicationUseCase.execute(user), SaveFileUtil.PDF);
    }
}
