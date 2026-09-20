package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.CannotUpdateNotRejectedFormException;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.exception.InvalidGradeException;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateFormUseCaseTest {

    @InjectMocks
    private UpdateFormUseCase updateFormUseCase;

    @Mock
    private FormFacade formFacade;

    @Mock
    private CalculateFormScoreService calculateFormScoreService;

    @Test
    void 원서를_수정한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.reject();
        User user = form.getUser();

        given(formFacade.getForm(user)).willReturn(form);

        // when
        updateFormUseCase.execute(user, FormFixture.createUpdateFormRequest(FormType.MEISTER_TALENT));

        // then
        verify(formFacade, times(1)).getForm(user);
        assertEquals(FormType.MEISTER_TALENT, form.getType());
    }

    @Test
    void 원서를_수정할_때_원서가_없으면_에러가_발생한다() {
        // given
        Long formId = 1L;
        User user = UserFixture.createUser();

        willThrow(new FormNotFoundException()).given(formFacade).getForm(user);

        // when and then
        assertThrows(FormNotFoundException.class, () ->
                updateFormUseCase.execute(user, FormFixture.createUpdateFormRequest(FormType.MEISTER_TALENT)));

        verify(formFacade, times(1)).getForm(user);
    }

    @Test
    void 원서를_수정할_때_본인의_원서가_아니면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.reject();
        User otherUser = UserFixture.createUser();

        given(formFacade.getForm(otherUser)).willReturn(form);

        // when and then
        assertThrows(AuthorityMismatchException.class, () ->
                updateFormUseCase.execute(otherUser, FormFixture.createUpdateFormRequest(FormType.MEISTER_TALENT))
        );

        verify(formFacade, times(1)).getForm(otherUser);
    }

    @Test
    void 원서를_수정할_때_반려된_원서가_아니면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        User user = form.getUser();

        given(formFacade.getForm(user)).willReturn(form);

        // when and then
        assertThrows(CannotUpdateNotRejectedFormException.class, () ->
                updateFormUseCase.execute(user, FormFixture.createUpdateFormRequest(FormType.MEISTER_TALENT))
        );

        verify(formFacade, times(1)).getForm(user);
    }

    @Test
    void 학기별_성적이_비어있는_원서로_수정하면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        SubmitFormRequest submitFormRequest = FormFixture.createFormRequestWithEmptyGrade(FormType.REGULAR);
        UpdateFormRequest request = new UpdateFormRequest(
                submitFormRequest.getApplicant(),
                submitFormRequest.getParent(),
                submitFormRequest.getEducation(),
                submitFormRequest.getGrade(),
                submitFormRequest.getDocument(),
                submitFormRequest.getType()
        );

        // when and then
        assertThrows(InvalidGradeException.class, () -> updateFormUseCase.execute(user, request));

        verify(formFacade, never()).getForm(any(User.class));
        verify(calculateFormScoreService, never()).execute(any(Form.class));
    }
}