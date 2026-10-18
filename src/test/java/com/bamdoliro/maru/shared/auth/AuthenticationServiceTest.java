package com.bamdoliro.maru.shared.auth;

import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.NativeWebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final AuthenticationExtractor extractor = mock(AuthenticationExtractor.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final UserFacade userFacade = mock(UserFacade.class);
    private final AuthSessionService authSessionService = mock(AuthSessionService.class);
    private final AuthenticationService service = new AuthenticationService(extractor, tokenService, userFacade, authSessionService);
    private final NativeWebRequest request = mock(NativeWebRequest.class);

    @Test
    void 리프레시_토큰으로는_유저를_인증하지_않는다() {
        when(extractor.extract(request)).thenReturn("refresh");
        when(tokenService.getType("refresh")).thenReturn(TokenType.REFRESH_TOKEN);
        assertThrows(InvalidTokenException.class, () -> service.getCurrentUser(request));
        verifyNoInteractions(userFacade);
    }

    @Test
    void 액세스_토큰의_UUID로_계정을_조회한다() {
        User user = UserFixture.createUser();
        when(extractor.extract(request)).thenReturn("access");
        when(tokenService.getType("access")).thenReturn(TokenType.ACCESS_TOKEN);
        when(tokenService.getUuid("access")).thenReturn(user.getUuid().toString());
        when(tokenService.getSessionId("access")).thenReturn("session");
        when(userFacade.getUserByUuid(user.getUuid())).thenReturn(user);
        assertSame(user, service.getCurrentUser(request));
        verify(authSessionService).validate(user.getUuid(), "session");
    }

    @Test
    void 폐기된_세션의_액세스_토큰을_거부한다() {
        User user = UserFixture.createUser();
        when(extractor.extract(request)).thenReturn("access");
        when(tokenService.getType("access")).thenReturn(TokenType.ACCESS_TOKEN);
        when(tokenService.getUuid("access")).thenReturn(user.getUuid().toString());
        when(tokenService.getSessionId("access")).thenReturn("revoked");
        doThrow(new ExpiredTokenException()).when(authSessionService).validate(user.getUuid(), "revoked");
        assertThrows(ExpiredTokenException.class, () -> service.getCurrentUser(request));
        verifyNoInteractions(userFacade);
    }
}
