package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.exception.InvalidGradeException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitFormUseCaseTest {

    @InjectMocks
    private SubmitFormUseCase submitFormUseCase;

    @Mock
    private FormRepository formRepository;

    @Mock
    private CalculateFormScoreService calculateFormScoreService;

    @Mock
    private AssignExaminationNumberService assignExaminationNumberService;

    @Test
    void 원서를_제출한다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(formRepository.findByUser(user)).willReturn(Optional.empty());

        willDoNothing().given(assignExaminationNumberService).execute(any(Form.class));
        willDoNothing().given(calculateFormScoreService).execute(any(Form.class));

        // when
        submitFormUseCase.execute(user, request);

        // then
        verify(formRepository, times(1)).findByUser(user);
        verify(calculateFormScoreService, times(1)).execute(any(Form.class));
        verify(assignExaminationNumberService, times(1)).execute(any(Form.class));
        verify(formRepository, times(1)).save(any(Form.class));
    }

    @Test
    void 원서를_제출할_때_이미_제출한_원서가_반려상태가_아니면서_있으면_에러가_발생한다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);

        given(formRepository.findByUser(user)).willReturn(Optional.of(form));

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> submitFormUseCase.execute(user, request));

        verify(formRepository, times(1)).findByUser(user);
        verify(calculateFormScoreService, never()).execute(any(Form.class));
        verify(assignExaminationNumberService, never()).execute(any(Form.class));
        verify(formRepository, never()).delete(any(Form.class));
        verify(formRepository, never()).save(any(Form.class));
    }

    @Test
    void 학기별_성적이_비어있는_원서를_제출하면_에러가_발생한다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequestWithEmptyGrade(FormType.REGULAR);
        User user = UserFixture.createUser();

        // when and then
        assertThrows(InvalidGradeException.class, () -> submitFormUseCase.execute(user, request));

        verify(formRepository, never()).findByUser(any(User.class));
        verify(calculateFormScoreService, never()).execute(any(Form.class));
        verify(assignExaminationNumberService, never()).execute(any(Form.class));
        verify(formRepository, never()).save(any(Form.class));
    }

    @Test
    void 검정고시_과목이_비어있는_원서를_제출하면_에러가_발생한다() {
        // given
        SubmitFormRequest request = FormFixture.createQualificationExaminationFormRequestWithEmptyGrade(FormType.REGULAR);
        User user = UserFixture.createUser();

        // when and then
        assertThrows(InvalidGradeException.class, () -> submitFormUseCase.execute(user, request));

        verify(formRepository, never()).findByUser(any(User.class));
        verify(calculateFormScoreService, never()).execute(any(Form.class));
        verify(assignExaminationNumberService, never()).execute(any(Form.class));
        verify(formRepository, never()).save(any(Form.class));
    }

    @Test
    void 저장할_때_원서_사용자_유니크_제약을_위반하면_이미_제출한_원서_에러로_변환한다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(formRepository.findByUser(user)).willReturn(Optional.empty());
        given(formRepository.save(any(Form.class))).willThrow(violation(
                "ERROR: duplicate key value violates unique constraint \"uk170dieqvc09ryf9hjk3kcpfkj\"\n"
                        + "  Detail: Key (user_id)=(1) already exists.",
                SubmitFormUseCase.FORM_USER_UNIQUE_CONSTRAINT
        ));

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> submitFormUseCase.execute(user, request));
    }

    @Test
    void 제약_이름을_얻지_못해도_사용자_중복_메시지이면_이미_제출한_원서_에러로_변환한다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();

        given(formRepository.findByUser(user)).willReturn(Optional.empty());
        given(formRepository.save(any(Form.class))).willThrow(violation(
                "ERROR: duplicate key value violates unique constraint\n"
                        + "  Detail: Key (user_id)=(1) already exists.",
                null
        ));

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> submitFormUseCase.execute(user, request));
    }

    @Test
    void 사용자_외래키_위반은_이미_제출한_원서_에러로_변환하지_않는다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();
        DataIntegrityViolationException exception = violation(
                "ERROR: insert or update on table \"tbl_form\" violates foreign key constraint \"fkm65a8f5rr6u9qyrp4lfh8dv04\"\n"
                        + "  Detail: Key (user_id)=(99999) is not present in table \"tbl_user\".",
                "fkm65a8f5rr6u9qyrp4lfh8dv04",
                "23503"
        );

        given(formRepository.findByUser(user)).willReturn(Optional.empty());
        given(formRepository.save(any(Form.class))).willThrow(exception);

        // when and then
        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> submitFormUseCase.execute(user, request)
        );
        assertSame(exception, thrown);
    }

    @Test
    void 수험번호_유니크_제약_위반은_이미_제출한_원서_에러로_변환하지_않는다() {
        // given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();
        DataIntegrityViolationException exception = violation(
                "ERROR: duplicate key value violates unique constraint \"uk34sdsp4bux2e7j1vy1ahqf3qh\"\n"
                        + "  Detail: Key (examination_number)=(C83E3QhikEBxahX1xBWRkA==) already exists.",
                "uk34sdsp4bux2e7j1vy1ahqf3qh"
        );

        given(formRepository.findByUser(user)).willReturn(Optional.empty());
        given(formRepository.save(any(Form.class))).willThrow(exception);

        // when and then
        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> submitFormUseCase.execute(user, request)
        );
        assertSame(exception, thrown);
    }

    private DataIntegrityViolationException violation(String message, String constraintName) {
        return violation(message, constraintName, "23505");
    }

    private DataIntegrityViolationException violation(String message, String constraintName, String sqlState) {
        SQLException sqlException = new SQLException(message, sqlState);
        return new DataIntegrityViolationException(
                "could not execute statement",
                new ConstraintViolationException("could not execute statement", sqlException, constraintName)
        );
    }
}
