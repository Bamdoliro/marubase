package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.log.AdminFromViewLog;
import com.bamdoliro.maru.domain.log.FormSubmitLog;
import com.bamdoliro.maru.domain.log.FormUpdateLog;
import com.bamdoliro.maru.domain.log.UpdatedField;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.AdminFormViewLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.FormSubmitLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.FormUpdateLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.UpdatedFieldRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.DeleteTestFormResponse;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class DeleteTestFormUseCaseTest {

    @Autowired
    private DeleteTestFormUseCase deleteTestFormUseCase;

    @Autowired
    private GenerateTestFormUseCase generateTestFormUseCase;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FormSubmitLogRepository formSubmitLogRepository;

    @Autowired
    private FormUpdateLogRepository formUpdateLogRepository;

    @Autowired
    private UpdatedFieldRepository updatedFieldRepository;

    @Autowired
    private AdminFormViewLogRepository adminFormViewLogRepository;

    @Test
    void 테스트_데이터인_원서와_사용자만_삭제한다() {
        // given
        User realUser = userRepository.save(UserFixture.createUser());
        Form realForm = saveForm(realUser);
        User admin = userRepository.save(UserFixture.createAdminUser());

        generateTestFormUseCase.execute(new GenerateTestFormListRequest(List.of(
                new GenerateTestFormRequest(FormType.REGULAR, 2),
                new GenerateTestFormRequest(FormType.MEISTER_TALENT, 1)
        )));
        User testUser = userRepository.findAll().stream()
                .filter(User::isTestData)
                .findFirst()
                .orElseThrow();
        Form testForm = formRepository.findByUser(testUser).orElseThrow();

        // 테스트 원서를 참조하는 로그와 실제 원서를 참조하는 로그
        formSubmitLogRepository.save(createFormSubmitLog(testUser, testForm));
        formSubmitLogRepository.save(createFormSubmitLog(realUser, realForm));
        FormUpdateLog testFormUpdateLog = formUpdateLogRepository.save(FormUpdateLog.builder()
                .phoneNumber(testUser.getPhoneNumber()).clientIp("127.0.0.1").userAgent("test")
                .user(testUser).form(testForm).build());
        updatedFieldRepository.save(UpdatedField.builder()
                .filedName("name").oldValue("a").newValue("b").formUpdateLog(testFormUpdateLog).build());
        adminFormViewLogRepository.save(AdminFromViewLog.builder()
                .clientIp("127.0.0.1").userAgent("test").user(admin).form(testForm).build());

        // when
        DeleteTestFormResponse response = deleteTestFormUseCase.execute();

        // then
        assertEquals(3, response.getDeletedFormCount());
        assertEquals(3, response.getDeletedUserCount());

        List<Form> remainingForms = formRepository.findAll();
        assertEquals(1, remainingForms.size());
        assertEquals(realForm.getId(), remainingForms.get(0).getId());
        assertEquals(2, userRepository.count());
        assertTrue(userRepository.findById(realUser.getId()).isPresent());
        assertTrue(userRepository.findById(admin.getId()).isPresent());

        assertEquals(1, formSubmitLogRepository.count());
        assertEquals(0, formUpdateLogRepository.count());
        assertEquals(0, updatedFieldRepository.count());
        assertEquals(0, adminFormViewLogRepository.count());
    }

    @Test
    void 테스트_데이터가_없으면_아무것도_삭제하지_않는다() {
        // given
        User realUser = userRepository.save(UserFixture.createUser());
        saveForm(realUser);

        // when
        DeleteTestFormResponse response = deleteTestFormUseCase.execute();

        // then
        assertEquals(0, response.getDeletedFormCount());
        assertEquals(0, response.getDeletedUserCount());
        assertEquals(1, formRepository.count());
        assertEquals(1, userRepository.count());
    }

    private Form saveForm(User user) {
        Form form = FormFixture.createRandomBusanForm(user);
        calculateFormScoreService.execute(form);
        form.assignExaminationNumber(1001L);
        return formRepository.save(form);
    }

    private FormSubmitLog createFormSubmitLog(User user, Form form) {
        return FormSubmitLog.builder()
                .phoneNumber(user.getPhoneNumber())
                .clientIp("127.0.0.1")
                .userAgent("test")
                .user(user)
                .form(form)
                .status(FormStatus.FINAL_SUBMITTED)
                .build();
    }
}
