package com.bamdoliro.maru.shared.auth;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationExtractor authenticationExtractor;
    private final TokenService tokenService;
    private final UserFacade userFacade;

    public User getCurrentUser(NativeWebRequest webRequest) {
        String token = authenticationExtractor.extract(webRequest);
        String phoneNumber = tokenService.getUuid(token);
        return userFacade.getUser(phoneNumber);
    }

}
