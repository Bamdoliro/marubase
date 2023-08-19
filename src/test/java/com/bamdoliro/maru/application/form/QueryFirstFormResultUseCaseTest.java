package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.response.FormResultResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryFirstFormResultUseCaseTest {

    @InjectMocks
    private QueryFirstFormResultUseCase queryFirstFormResultUseCase;

    @Mock
    private FormFacade formFacade;

    @Test
    void 합격자가_1차_결과를_조회한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.firstPass();
        given(formFacade.getForm(form.getUser())).willReturn(form);

        // when
        FormResultResponse response = queryFirstFormResultUseCase.execute(form.getUser());

        // then
        assertTrue(response.isPassed());
        verify(formFacade, times(1)).getForm(form.getUser());
    }

    @Test
    void 불합격자가_1차_결과를_조회한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.firstFail();
        given(formFacade.getForm(form.getUser())).willReturn(form);

        // when
        FormResultResponse response = queryFirstFormResultUseCase.execute(form.getUser());

        // then
        assertFalse(response.isPassed());
        verify(formFacade, times(1)).getForm(form.getUser());
    }

    @Test
    void 결과를_조회할_때_원서_접수를_하지_않았다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        willThrow(FormNotFoundException.class).given(formFacade).getForm(user);

        // when and then
        assertThrows(FormNotFoundException.class,
                () -> queryFirstFormResultUseCase.execute(user));

        verify(formFacade, times(1)).getForm(user);
    }
}