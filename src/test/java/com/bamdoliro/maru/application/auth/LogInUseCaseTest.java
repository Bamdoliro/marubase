package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.domain.auth.exception.WrongLoginException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
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
        LogInRequest request = new LogInRequest(user.getPhoneNumber(), "비밀번호");
        String accessToken = AuthFixture.createAccessTokenString();
        String refreshToken = AuthFixture.createRefreshTokenString();

        given(userFacade.getUser(request.getPhoneNumber())).willReturn(user);
        given(tokenService.generateAccessToken(user.getPhoneNumber())).willReturn(accessToken);
        given(tokenService.generateRefreshToken(user.getPhoneNumber())).willReturn(refreshToken);

        // when
        TokenResponse response = logInUseCase.execute(request);

        // then
        verify(userFacade, times(1)).getUser(request.getPhoneNumber());
        verify(tokenService, times(1)).generateAccessToken(user.getPhoneNumber());
        verify(tokenService, times(1)).generateRefreshToken(user.getPhoneNumber());
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    void 유저가_없으면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getPhoneNumber(), "비밀번호");

        given(userFacade.getUser(request.getPhoneNumber())).willThrow(UserNotFoundException.class);

        // when and then
        assertThrows(WrongLoginException.class, () -> logInUseCase.execute(request));

        verify(userFacade, times(1)).getUser(request.getPhoneNumber());
        verify(tokenService, never()).generateAccessToken(user.getPhoneNumber());
        verify(tokenService, never()).generateRefreshToken(user.getPhoneNumber());
    }

    @Test
    void 비밀번호가_틀리면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        LogInRequest request = new LogInRequest(user.getPhoneNumber(), "틀린비밀번호");

        given(userFacade.getUser(request.getPhoneNumber())).willReturn(user);

        // when and then
        assertThrows(WrongLoginException.class, () -> logInUseCase.execute(request));

        verify(userFacade, times(1)).getUser(request.getPhoneNumber());
        verify(tokenService, never()).generateAccessToken(user.getPhoneNumber());
        verify(tokenService, never()).generateRefreshToken(user.getPhoneNumber());
    }
}