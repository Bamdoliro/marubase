package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.exception.CannotUpdateNotRejectedFormException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateFormUseCaseTest {

    @InjectMocks
    private UpdateFormUseCase updateFormUseCase;

    @Mock
    private UserFacade userFacade;

    @Mock
    private FormFacade formFacade;

    @Test
    void 원서를_수정한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.reject();
        User user = form.getUser();

        given(userFacade.getCurrentUser()).willReturn(user);
        given(formFacade.getForm(form.getId())).willReturn(form);

        // when
        updateFormUseCase.execute(form.getId(), FormFixture.createFormRequest(FormType.MEISTER_TALENT));

        // then
        verify(userFacade, times(1)).getCurrentUser();
        verify(formFacade, times(1)).getForm(form.getId());
        assertEquals(form.getType(), FormType.MEISTER_TALENT);
    }

    @Test
    void 원서를_수정할_때_원서가_없으면_에러가_발생한다() {
        // given
        Long formId = 1L;
        User user = UserFixture.createUser();
        given(userFacade.getCurrentUser()).willReturn(user);
        willThrow(new FormNotFoundException()).given(formFacade).getForm(formId);

        // when and then
        assertThrows(FormNotFoundException.class, () ->
                updateFormUseCase.execute(formId, FormFixture.createFormRequest(FormType.MEISTER_TALENT)));

        verify(userFacade, times(1)).getCurrentUser();
        verify(formFacade, times(1)).getForm(formId);
    }

    @Test
    void 원서를_수정할_때_본인의_원서가_아니면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.reject();
        User otherUser = UserFixture.createUser();

        given(userFacade.getCurrentUser()).willReturn(otherUser);
        given(formFacade.getForm(form.getId())).willReturn(form);

        // when and then
        assertThrows(AuthorityMismatchException.class, () -> {
            updateFormUseCase.execute(form.getId(), FormFixture.createFormRequest(FormType.MEISTER_TALENT));
        });
        verify(userFacade, times(1)).getCurrentUser();
        verify(formFacade, times(1)).getForm(form.getId());
    }

    @Test
    void 원서를_수정할_때_반려된_원서가_아니면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        User user = form.getUser();

        given(userFacade.getCurrentUser()).willReturn(user);
        given(formFacade.getForm(form.getId())).willReturn(form);

        // when and then
        assertThrows(CannotUpdateNotRejectedFormException.class, () -> {
            updateFormUseCase.execute(form.getId(), FormFixture.createFormRequest(FormType.MEISTER_TALENT));
        });
        verify(userFacade, times(1)).getCurrentUser();
        verify(formFacade, times(1)).getForm(form.getId());
    }
}