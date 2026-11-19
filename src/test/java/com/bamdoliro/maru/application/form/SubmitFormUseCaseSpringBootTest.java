package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.config.DatabaseClearExtension;
import com.bamdoliro.maru.shared.config.TimeConfig;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class SubmitFormUseCaseSpringBootTest {

    @Autowired
    private SubmitFormUseCase submitFormUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private AssignExaminationNumberService assignExaminationNumberService;

    @MockitoSpyBean
    private FormRepository formRepository;

    @Autowired
    private AdmissionScheduleRepository admissionScheduleRepository;

    @Autowired
    private EntityManager entityManager;

    // 원서 접수 기간 안의 시각으로 고정한다.
    @TestBean(name = "serviceClock")
    private Clock serviceClock;

    static Clock serviceClock() {
        return Clock.fixed(
                LocalDateTime.of(2025, 10, 21, 12, 0).atZone(TimeConfig.SERVICE_ZONE_ID).toInstant(),
                TimeConfig.SERVICE_ZONE_ID
        );
    }

    @BeforeEach
    void setUp() {
        admissionScheduleRepository.save(ScheduleFixture.createSchedule());
    }

    @Test
    void 사용자_유니크_제약_위반을_변환해도_같은_트랜잭션의_쓰기는_롤백된다() {
        // given
        User user = userRepository.save(UserFixture.createUser());
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        AtomicLong formCountInTransaction = new AtomicLong();

        // 실제 INSERT를 실행한 뒤 운영 DB 형식의 사용자 유니크 제약 위반을 던진다.
        doAnswer(invocation -> {
            entityManager.persist(invocation.getArgument(0));
            entityManager.flush();
            formCountInTransaction.set(formRepository.count());
            throw userUniqueViolation();
        }).when(formRepository).save(any(Form.class));

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> submitFormUseCase.execute(user, request));
        assertEquals(1L, formCountInTransaction.get());
        assertEquals(0L, formRepository.count());
    }

    @Test
    void 운영_제약_이름과_형식이_다른_위반은_변환하지_않고_롤백된다() {
        // given
        User user = userRepository.save(UserFixture.createUser());
        Form submittedForm = FormFixture.createRandomBusanForm(user);
        calculateFormScoreService.execute(submittedForm);
        assignExaminationNumberService.execute(submittedForm);
        formRepository.save(submittedForm);
        Long submittedExaminationNumber = submittedForm.getExaminationNumber();
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.MEISTER_TALENT);

        // 사전 검사를 통과시켜 H2의 실제 user_id 유니크 제약 위반을 일으킨다.
        doReturn(Optional.empty()).when(formRepository).findByUser(any(User.class));

        // when and then
        assertThrows(DataIntegrityViolationException.class, () -> submitFormUseCase.execute(user, request));
        assertEquals(1L, formRepository.count());
        assertEquals(submittedExaminationNumber, formRepository.findAll().get(0).getExaminationNumber());
    }

    private DataIntegrityViolationException userUniqueViolation() {
        SQLException sqlException = new SQLException(
                "ERROR: duplicate key value violates unique constraint \"uk170dieqvc09ryf9hjk3kcpfkj\"\n"
                        + "  Detail: Key (user_id)=(1) already exists.",
                "23505"
        );
        return new DataIntegrityViolationException(
                "could not execute statement",
                new ConstraintViolationException(
                        "could not execute statement",
                        sqlException,
                        SubmitFormUseCase.FORM_USER_UNIQUE_CONSTRAINT
                )
        );
    }
}
