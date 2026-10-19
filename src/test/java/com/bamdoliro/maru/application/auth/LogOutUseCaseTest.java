package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogOutUseCaseTest {

    @InjectMocks
    private LogOutUseCase logOutUseCase;

    @Mock
    private AuthSessionService authSessionService;

    @Mock
    private UserFacade userFacade;

    @Test
    void 로그아웃_한다() {
        // given
        User user = UserFixture.createUser();
        willDoNothing().given(authSessionService).revoke(user.getUuid());

        // when
        logOutUseCase.execute(user);

        // then
        verify(userFacade).getUserByUuidForUpdate(user.getUuid());
        verify(authSessionService, times(1)).revoke(user.getUuid());
    }
}
