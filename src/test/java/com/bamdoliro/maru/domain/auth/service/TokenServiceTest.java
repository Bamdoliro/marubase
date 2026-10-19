package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtProperties jwtProperties;

    @Test
    void 액세스_토큰을_생성한다() {
        // given
        User user = UserFixture.createUser();
        given(jwtProperties.getAccessExpirationTime()).willReturn(1000L);
        given(jwtProperties.getSecretKey()).willReturn("탑시크릿정말정말탑시크릿진짜옝용찐찐찐찐찐이야");

        // when
        String accessToken = tokenService.generateAccessToken(user.getUuid().toString(), java.util.UUID.randomUUID().toString());

        // then
        verify(jwtProperties, times(1)).getAccessExpirationTime();
        verify(jwtProperties, times(1)).getSecretKey();
        assertNotNull(accessToken);
    }

    @Test
    void 리프레시_토큰을_생성한다() {
        // given
        User user = UserFixture.createUser();
        given(jwtProperties.getRefreshExpirationTime()).willReturn(1000L);
        given(jwtProperties.getSecretKey()).willReturn("탑시크릿정말정말탑시크릿진짜옝용찐찐찐찐찐이야");

        // when
        String refreshToken = tokenService.generateRefreshToken(user.getUuid().toString(), java.util.UUID.randomUUID().toString());

        // then
        verify(jwtProperties, times(1)).getRefreshExpirationTime();
        verify(jwtProperties, times(1)).getSecretKey();
        assertNotNull(refreshToken);
    }
}
