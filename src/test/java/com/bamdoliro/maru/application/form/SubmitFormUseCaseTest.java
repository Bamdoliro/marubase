package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubmitFormUseCaseTest {

    @InjectMocks
    private SubmitFormUseCase submitFormUseCase;

    @Mock
    private FormFacade formFacade;


    @Test
    void 원서를_제출한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        SubmitFormRequest request = new SubmitFormRequest("https://maru.bamdoliro.com/form.pdf");

        given(formFacade.getForm(any(User.class))).willReturn(form);

        // when
        submitFormUseCase.execute(form.getUser(), request);

        // then
        assertEquals(form.getFormUrl(), request.getFormUrl());
        verify(formFacade, times(1)).getForm(any(User.class));
    }

    @Test
    void 원서를_제출할_때_이미_제출했다면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.approve();
        SubmitFormRequest request = new SubmitFormRequest("https://maru.bamdoliro.com/form.pdf");

        given(formFacade.getForm(any(User.class))).willReturn(form);

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> submitFormUseCase.execute(form.getUser(), request));

        assertNull(form.getFormUrl());
        verify(formFacade, times(1)).getForm(any(User.class));
    }
}