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
class RejectFormUseCaseTest {

    @InjectMocks
    private RejectFormUseCase rejectFormUseCase;

    @Mock
    private FormFacade formFacade;

    @Test
    void 원서를_반려한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formFacade.getForm(form.getId())).willReturn(form);

        // when
        rejectFormUseCase.execute(form.getId());

        // then
        verify(formFacade).getForm(form.getId());
        assertEquals(form.getStatus(), FormStatus.REJECTED);
    }

    @Test
    void 원서를_반려할_때_원서가_없으면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.submit();
        willThrow(new FormNotFoundException()).given(formFacade).getForm(form.getId());

        // when and then
        assertThrows(FormNotFoundException.class, () -> rejectFormUseCase.execute(form.getId()));
        assertEquals(form.getStatus(), FormStatus.FINAL_SUBMITTED);
    }
}