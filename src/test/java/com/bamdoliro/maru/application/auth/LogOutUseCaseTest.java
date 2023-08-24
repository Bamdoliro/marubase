package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
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
    private TokenRepository tokenRepository;

    @Test
    void 로그아웃_한다() {
        // given
        User user = UserFixture.createUser();
        willDoNothing().given(tokenRepository).deleteById(user.getPhoneNumber());

        // when
        logOutUseCase.execute(user);

        // then
        verify(tokenRepository, times(1)).deleteById(user.getPhoneNumber());
    }
}