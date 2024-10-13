package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void 원서를_제출할_때_이미_제출한_원서가_반려상태면_다시_작성한다() {
        //given
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.reject();

        given(formRepository.findByUser(user)).willReturn(Optional.of(form));

        //when
        submitFormUseCase.execute(user, request);

        //then
        verify(formRepository, times(1)).findByUser(user);
        verify(calculateFormScoreService, times(1)).execute(any(Form.class));
        verify(assignExaminationNumberService, times(1)).execute(any(Form.class));
        verify(formRepository, times(1)).delete(any(Form.class));
        verify(formRepository, times(1)).save(any(Form.class));
    }
}