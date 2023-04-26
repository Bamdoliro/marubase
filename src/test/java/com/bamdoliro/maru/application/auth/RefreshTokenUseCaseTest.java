package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.domain.Token;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
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
    void 리프레시_토큰으로_액세스_토큰을_재발급_한다() {
        // given
        Token refreshToken = AuthFixture.createRefreshToken();
        String accessToken = AuthFixture.createAccessTokenString();
        given(tokenRepository.findByToken(refreshToken.getToken())).willReturn(Optional.of(refreshToken));
        given(tokenService.generateAccessToken(refreshToken.getEmail())).willReturn(accessToken);

        // when
        TokenResponse response = refreshTokenUseCase.execute(refreshToken.getToken());

        // then
        verify(tokenRepository, times(1)).findByToken(refreshToken.getToken());
        verify(tokenService, times(1)).generateAccessToken(refreshToken.getEmail());
        assertEquals(accessToken, response.getAccessToken());
    }

    @Test
    void 리프레시_토큰이_만료되었으면_에러가_발생한다() {
        // given
        given(tokenRepository.findByToken("만료.리프레시.토큰")).willReturn(Optional.empty());

        // when and then
        assertThrows(ExpiredTokenException.class, () -> refreshTokenUseCase.execute("만료.리프레시.토큰"));
        verify(tokenService, never()).generateAccessToken(anyString());
    }
}