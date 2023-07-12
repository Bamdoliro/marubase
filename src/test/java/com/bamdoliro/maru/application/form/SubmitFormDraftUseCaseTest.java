package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormDraftRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubmitFormDraftUseCaseTest {

    @InjectMocks
    private SubmitFormDraftUseCase submitFormDraftUseCase;

    @Mock
    private FormRepository formRepository;

    @Mock
    private FormService formService;

    @Test
    void 원서를_접수한다() {
        // given
        SubmitFormDraftRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();
        
        given(formRepository.existsByUserId(user.getId())).willReturn(false);
        willDoNothing().given(formService).calculateScore(any(Form.class));

        // when
        submitFormDraftUseCase.execute(user, request);

        // then
        
        verify(formRepository, times(1)).existsByUserId(user.getId());
        verify(formService, times(1)).calculateScore(any(Form.class));
        verify(formRepository, times(1)).save(any(Form.class));
    }

    @Test
    void 원서를_접수할_때_이미_접수한_원서가_있으면_에러가_발생한다() {
        // given
        SubmitFormDraftRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        User user = UserFixture.createUser();
        
        given(formRepository.existsByUserId(user.getId())).willReturn(true);

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> submitFormDraftUseCase.execute(user, request));
        
        verify(formRepository, times(1)).existsByUserId(user.getId());
        verify(formService, never()).calculateScore(any(Form.class));
        verify(formRepository, never()).save(any(Form.class));
    }

}