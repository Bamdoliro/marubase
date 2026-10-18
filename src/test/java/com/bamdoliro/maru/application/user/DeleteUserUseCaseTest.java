package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.application.auth.LogOutUseCase;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.DeleteUserRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private FormRepository formRepository;

    @Mock
    private LogOutUseCase logOutUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFacade userFacade;

    @Test
    void 유저를_삭제한다() {
        // given
        User user = UserFixture.createUser();
        given(userFacade.getUserByUuidForUpdate(user.getUuid())).willReturn(user);
        Form form = FormFixture.createForm(FormType.REGULAR);
        DeleteUserRequest request = new DeleteUserRequest("비밀번호");

        given(formRepository.findByUser(user)).willReturn(Optional.of(form));
        willDoNothing().given(formRepository).delete(form);
        willDoNothing().given(logOutUseCase).execute(user);
        willDoNothing().given(userRepository).delete(user);

        deleteUserUseCase.execute(user, request);

        verify(formRepository, times(1)).findByUser(user);
        verify(formRepository, times(1)).delete(form);
        verify(logOutUseCase, times(1)).execute(user);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void 유저를_삭제할_때_비밀번호가_틀리면_에러가_발생한다() {
        User user = UserFixture.createUser();
        given(userFacade.getUserByUuidForUpdate(user.getUuid())).willReturn(user);
        Form form = FormFixture.createForm(FormType.REGULAR);
        DeleteUserRequest request = new DeleteUserRequest("틀린 비밀번호");

        assertThrows(PasswordMismatchException.class, () -> deleteUserUseCase.execute(user, request));

        verify(formRepository, never()).findByUser(user);
        verify(formRepository, never()).delete(form);
        verify(logOutUseCase, never()).execute(user);
        verify(userRepository, never()).delete(user);
    }
}
