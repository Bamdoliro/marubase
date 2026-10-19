package com.bamdoliro.maru.shared.auth;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationExtractor authenticationExtractor;
    private final TokenService tokenService;
    private final UserFacade userFacade;
    private final AuthSessionService authSessionService;

    public User getCurrentUser(NativeWebRequest webRequest) {
        String token = authenticationExtractor.extract(webRequest);
        if (tokenService.getType(token) != TokenType.ACCESS_TOKEN) {
            throw new InvalidTokenException();
        }
        UUID uuid = UUID.fromString(tokenService.getUuid(token));
        authSessionService.validate(uuid, tokenService.getSessionId(token));
        return userFacade.getUserByUuid(uuid);
    }

}
