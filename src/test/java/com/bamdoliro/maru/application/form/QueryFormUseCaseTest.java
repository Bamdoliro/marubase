package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.presentation.form.dto.response.FormResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryFormUseCaseTest {

    @InjectMocks
    private QueryFormUseCase queryFormUseCase;

    @Mock
    private FormFacade formFacade;

    @Test
    void 원서를_조회한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        User user = form.getUser();
        Long id = user.getId();

        
        given(formFacade.getForm(id)).willReturn(form);

        // when
        FormResponse response = queryFormUseCase.execute(user, id);

        // then
        
        verify(formFacade, times(1)).getForm(id);
        assertEquals(form.getId(), response.getId());
        assertEquals(form.getApplicant().getName(), response.getApplicant().getName());
        assertEquals(form.getParent().getName(), response.getParent().getName());
        assertEquals(form.getDocument().getCoverLetter(), response.getDocument().getCoverLetter());
    }

    @Test
    void 원서를_조회할_때_본인의_원서가_아니라면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        User otherUser = UserFixture.createUser();

        given(formFacade.getForm(form.getId())).willReturn(form);

        // when and then
        assertThrows(AuthorityMismatchException.class, () -> queryFormUseCase.execute(otherUser, form.getId()));
        
        verify(formFacade, times(1)).getForm(form.getId());
    }

    @Test
    void 원서를_조회할_때_없는_원서라면_에러가_발생한다() {
        // given
        Long id = -1L;
        User user = UserFixture.createUser();

        
        willThrow(new FormNotFoundException()).given(formFacade).getForm(id);

        // when and then
        assertThrows(FormNotFoundException.class, () -> queryFormUseCase.execute(user, id));
        
        verify(formFacade, times(1)).getForm(id);
    }
}