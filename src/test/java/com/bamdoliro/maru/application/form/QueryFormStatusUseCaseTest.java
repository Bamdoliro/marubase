package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
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
class QueryFormStatusUseCaseTest {

    @InjectMocks
    private QueryFormStatusUseCase queryFormStatusUseCase;

    @Mock
    private FormFacade formFacade;

    @Test
    void 원서_상태를_조회한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        User user = form.getUser();
        Long id = user.getId();


        given(formFacade.getForm(user)).willReturn(form);

        // when
        FormSimpleResponse response = queryFormStatusUseCase.execute(user);

        // then

        verify(formFacade, times(1)).getForm(user);
        assertEquals(form.getStatus(), response.getStatus());
        assertEquals(form.getType(), response.getType());
    }

    @Test
    void 원서_상태를_조회할_때_없는_원서라면_에러가_발생한다() {
        // given
        Long id = -1L;
        User user = UserFixture.createUser();


        willThrow(new FormNotFoundException()).given(formFacade).getForm(user);

        // when and then
        assertThrows(FormNotFoundException.class, () -> queryFormStatusUseCase.execute(user));

        verify(formFacade, times(1)).getForm(user);
    }
}