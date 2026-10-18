package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenUseCaseTest {

    private final TokenService tokenService = mock(TokenService.class);
    private final AuthSessionService sessions = mock(AuthSessionService.class);
    private final RefreshTokenUseCase useCase = new RefreshTokenUseCase(tokenService, sessions);

    @Test
    void 살아있는_세션에서만_동일한_세션의_액세스_토큰을_재발급한다() {
        UUID uuid = stubRefresh();
        when(tokenService.generateAccessToken(uuid.toString(), "session")).thenReturn("access");
        assertEquals("access", useCase.execute("refresh").getAccessToken());
        verify(sessions).validate(uuid, "session");
        verify(sessions, never()).create(any());
    }

    @Test
    void 폐기된_세션으로_재발급하지_않는다() {
        UUID uuid = stubRefresh();
        doThrow(new ExpiredTokenException()).when(sessions).validate(uuid, "session");
        assertThrows(ExpiredTokenException.class, () -> useCase.execute("refresh"));
        verify(tokenService, never()).generateAccessToken(any(), any());
    }

    @Test
    void Redis_장애시_재발급하지_않는다() {
        UUID uuid = stubRefresh();
        doThrow(new IllegalStateException("Redis unavailable")).when(sessions).validate(uuid, "session");
        assertThrows(IllegalStateException.class, () -> useCase.execute("refresh"));
        verify(tokenService, never()).generateAccessToken(any(), any());
    }

    @Test
    void 액세스_토큰으로_재발급하지_않는다() {
        when(tokenService.getType("access")).thenReturn(TokenType.ACCESS_TOKEN);
        assertThrows(InvalidTokenException.class, () -> useCase.execute("access"));
        verifyNoInteractions(sessions);
    }

    private UUID stubRefresh() {
        UUID uuid = UUID.randomUUID();
        when(tokenService.getType("refresh")).thenReturn(TokenType.REFRESH_TOKEN);
        when(tokenService.getUuid("refresh")).thenReturn(uuid.toString());
        when(tokenService.getSessionId("refresh")).thenReturn("session");
        return uuid;
    }
}
