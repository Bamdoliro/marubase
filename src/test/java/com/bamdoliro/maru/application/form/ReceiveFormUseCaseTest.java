package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReceiveFormUseCaseTest {

    @InjectMocks
    private ReceiveFormUseCase receiveFormUseCase;

    @Mock
    private FormFacade formFacade;

    @Test
    void 원서를_접수한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formFacade.getForm(form.getId())).willReturn(form);

        // when
        receiveFormUseCase.execute(form.getId());

        // then
        verify(formFacade).getForm(form.getId());
        assertEquals(form.getStatus(), FormStatus.RECEIVED);
    }

    @Test
    void 원서를_접수할_때_원서가_없으면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.approve();
        willThrow(new FormNotFoundException()).given(formFacade).getForm(form.getId());

        // when and then
        assertThrows(FormNotFoundException.class, () -> receiveFormUseCase.execute(form.getId()));
        assertEquals(form.getStatus(), FormStatus.APPROVED);
    }
}