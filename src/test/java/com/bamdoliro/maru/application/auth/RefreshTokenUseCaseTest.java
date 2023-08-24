package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.domain.Token;
import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @Test
    void 리프레시_토큰으로_액세스_토큰을_재발급한다() {
        // given
        Token refreshToken = AuthFixture.createRefreshToken();
        String accessToken = AuthFixture.createAccessTokenString();
        given(tokenService.getType(refreshToken.getToken())).willReturn(TokenType.REFRESH_TOKEN.name());
        given(tokenService.getUuid(refreshToken.getToken())).willReturn(refreshToken.getUuid());
        given(tokenRepository.findById(refreshToken.getUuid())).willReturn(Optional.of(refreshToken));
        given(tokenService.generateAccessToken(refreshToken.getUuid())).willReturn(accessToken);

        // when
        TokenResponse response = refreshTokenUseCase.execute(refreshToken.getToken());

        // then
        verify(tokenService, times(1)).getType(refreshToken.getToken());
        verify(tokenService, times(1)).getUuid(refreshToken.getToken());
        verify(tokenRepository, times(1)).findById(refreshToken.getUuid());
        verify(tokenService, times(1)).generateAccessToken(refreshToken.getUuid());
        assertEquals(accessToken, response.getAccessToken());
    }

    @Test
    void 리프레시_토큰이_만료되었으면_에러가_발생한다() {
        // given
        String expiredRefreshToken = "만료.리프레시.토큰";
        doThrow(new ExpiredTokenException()).when(tokenService).getType(expiredRefreshToken);

        // when and then
        assertThrows(ExpiredTokenException.class, () -> refreshTokenUseCase.execute(expiredRefreshToken));
        verify(tokenService, times(1)).getType(expiredRefreshToken);
        verify(tokenService, never()).generateAccessToken(anyString());
    }

    @Test
    void 로그아웃_했으면_에러가_발생한다() {
        // given
        String loggedOutRefreshToken = "로그아웃.리프레시.토큰";
        Token refreshToken = AuthFixture.createRefreshToken();
        given(tokenService.getType(loggedOutRefreshToken)).willReturn(TokenType.REFRESH_TOKEN.name());
        given(tokenService.getUuid(loggedOutRefreshToken)).willReturn(refreshToken.getUuid());
        given(tokenRepository.findById(refreshToken.getUuid())).willReturn(Optional.of(refreshToken));

        // when and then
        assertThrows(ExpiredTokenException.class, () -> refreshTokenUseCase.execute(loggedOutRefreshToken));
    }

    @Test
    void 액세스_토큰으로_재발급을_시도하면_에러가_발생한다() {
        // given
        String accessToken = "이것은.액세스.토큰";
        given(tokenService.getType(accessToken)).willReturn(TokenType.ACCESS_TOKEN.name());

        // when and then
        assertThrows(InvalidTokenException.class, () -> refreshTokenUseCase.execute(accessToken));
        verify(tokenService, times(1)).getType(accessToken);
        verify(tokenRepository, never()).findById(anyString());
        verify(tokenService, never()).generateAccessToken(anyString());
    }
}