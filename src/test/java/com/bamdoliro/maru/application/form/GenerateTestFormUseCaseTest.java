package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.GenerateTestFormResponse;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class GenerateTestFormUseCaseTest {

    @Autowired
    private GenerateTestFormUseCase generateTestFormUseCase;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    void 전형별로_요청한_개수만큼_최종_제출된_테스트_원서를_생성한다() {
        // given
        GenerateTestFormListRequest request = new GenerateTestFormListRequest(List.of(
                new GenerateTestFormRequest(FormType.REGULAR, 3),
                new GenerateTestFormRequest(FormType.ONE_PARENT, 2),
                new GenerateTestFormRequest(FormType.REGULAR, 1)
        ));

        // when
        GenerateTestFormResponse response = generateTestFormUseCase.execute(request);

        // then
        assertEquals(6, response.getTotalCount());
        assertEquals(4, response.getCountByType().get(FormType.REGULAR));
        assertEquals(2, response.getCountByType().get(FormType.ONE_PARENT));

        List<Form> forms = formRepository.findAll();
        assertEquals(6, forms.size());
        assertEquals(4, forms.stream().filter(form -> form.getType() == FormType.REGULAR).count());
        assertEquals(2, forms.stream().filter(form -> form.getType() == FormType.ONE_PARENT).count());
        forms.forEach(form -> {
            assertEquals(FormStatus.FINAL_SUBMITTED, form.getStatus());
            assertTrue(form.getUser().isTestData());
            assertNotNull(form.getScore());
            assertNotNull(form.getExaminationNumber());
            assertEquals(form.getUser().getPhoneNumber(), form.getApplicant().getPhoneNumber().getValue());
        });
        assertEquals(6, forms.stream().map(Form::getExaminationNumber).distinct().count());
        assertEquals(6, userRepository.findAll().stream().map(user -> user.getPhoneNumber()).distinct().count());
    }
}
