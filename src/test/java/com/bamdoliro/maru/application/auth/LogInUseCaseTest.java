package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogInUseCaseTest {

    @InjectMocks
    private LogInUseCase logInUseCase;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserFacade userFacade;

    @Test
    void 유저가_로그인한다() {
        // given
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getEmail(), "비밀번호");
        String accessToken = AuthFixture.createAccessToken();
        String refreshToken = AuthFixture.createRefreshToken();

        given(userFacade.getUser(request.getEmail())).willReturn(user);
        given(tokenService.generateAccessToken(user.getEmail())).willReturn(accessToken);
        given(tokenService.generateRefreshToken(user.getEmail())).willReturn(refreshToken);

        // when
        TokenResponse response = logInUseCase.execute(request);

        // then
        verify(userFacade, times(1)).getUser(request.getEmail());
        verify(tokenService, times(1)).generateAccessToken(user.getEmail());
        verify(tokenService, times(1)).generateRefreshToken(user.getEmail());
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    void 유저가_없으면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getEmail(), "비밀번호");

        given(userFacade.getUser(request.getEmail())).willThrow(UserNotFoundException.class);

        // when and then
        assertThrows(UserNotFoundException.class, () -> logInUseCase.execute(request));

        verify(userFacade, times(1)).getUser(request.getEmail());
        verify(tokenService, never()).generateAccessToken(user.getEmail());
        verify(tokenService, never()).generateRefreshToken(user.getEmail());
    }

    @Test
    void 비밀번호가_틀리면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getEmail(), "틀린비밀번호");

        given(userFacade.getUser(request.getEmail())).willReturn(user);

        // when and then
        assertThrows(PasswordMismatchException.class, () -> logInUseCase.execute(request));

        verify(userFacade, times(1)).getUser(request.getEmail());
        verify(tokenService, never()).generateAccessToken(user.getEmail());
        verify(tokenService, never()).generateRefreshToken(user.getEmail());
    }
}